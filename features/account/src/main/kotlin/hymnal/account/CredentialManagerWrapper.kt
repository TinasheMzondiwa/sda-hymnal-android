// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.account

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/** A wrapper around [CredentialManager] for easier testing. */
interface CredentialManagerWrapper {
    suspend fun getCredential(
        context: Context,
        request: GetCredentialRequest,
    ): GetCredentialResponse

    suspend fun clearCredentialState(request: ClearCredentialStateRequest)
}

@ContributesBinding(scope = AppScope::class)
@Inject
class CredentialManagerWrapperImpl(
    context: Context,
) : CredentialManagerWrapper {
    private val credentialManager: CredentialManager by lazy { CredentialManager.create(context) }

    override suspend fun getCredential(context: Context, request: GetCredentialRequest): GetCredentialResponse {
        return credentialManager.getCredential(context, request)
    }

    override suspend fun clearCredentialState(request: ClearCredentialStateRequest) {
        credentialManager.clearCredentialState(request)
    }
}
