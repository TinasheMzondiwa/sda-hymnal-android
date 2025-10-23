/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package services.hymnal.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

fun FirebaseAuth.isUserSignedIn(): Boolean = this.currentUser?.isAnonymous == false

fun FirebaseAuth.signedInUser(): FirebaseUser? =
    if (currentUser?.isAnonymous == true) null else currentUser

@Throws(IllegalStateException::class)
suspend fun FirebaseAuth.checkAuth() {
    if (signedInUser() == null) {
        val result = signInAnonymously().await()
        if (result.user == null) {
            throw kotlin.IllegalStateException("Anonymous auth failed...")
        }
    }
}

suspend fun FirebaseAuth.authedUserId(): String {
    return currentUser?.uid ?: run {
        checkAuth()
        currentUser?.uid ?: throw kotlin.IllegalStateException("User ID is null after auth check")
    }
}