// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package services.hymnal.firebase

interface FirebaseContentSync {
    fun signedIn()
    fun signedOut()
    suspend fun deleteAccount()
}