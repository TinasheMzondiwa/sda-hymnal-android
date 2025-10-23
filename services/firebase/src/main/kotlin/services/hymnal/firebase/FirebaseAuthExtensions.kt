/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package services.hymnal.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

/**
 * Extension function on FirebaseAuth to expose authentication state changes as a Flow.
 *
 * @return A Flow<FirebaseUser?> which emits the current user whenever the auth state changes.
 */
fun FirebaseAuth.userFlow(): Flow<FirebaseUser?> = callbackFlow {
    // Listener that receives the user object on every state change (sign in, sign out)
    val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }

    // Attach the listener
    addAuthStateListener(authStateListener)

    // Suspension block: ensures the listener is removed when the Flow is closed or cancelled.
    awaitClose {
        removeAuthStateListener(authStateListener)
    }
}