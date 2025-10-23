/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package services.hymnal.firebase.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object FirebaseBindings {

    @Provides
    @SingleIn(AppScope::class)
    fun providesAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @SingleIn(AppScope::class)
    fun providesFirestore(): FirebaseFirestore = Firebase.firestore
}