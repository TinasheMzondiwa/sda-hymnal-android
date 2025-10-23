// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.account

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.model.HymnalAppConfig
import hymnal.libraries.navigation.AccountScreen
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import services.hymnal.firebase.userFlow
import timber.log.Timber

@AssistedInject
class AccountPresenter(
    @Assisted private val navigator: Navigator,
    private val appConfig: HymnalAppConfig,
    private val firebaseAuth: FirebaseAuth,
    private val credentialManager: CredentialManagerWrapper,
    private val dispatcherProvider: DispatcherProvider,
) : Presenter<State> {

    private val getCredentialRequest: GetCredentialRequest by lazy {
        GetCredentialRequest.Builder()
            .addCredentialOption(GetSignInWithGoogleOption.Builder(appConfig.webClientId).build())
            .build()
    }

    @CircuitInject(AccountScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): AccountPresenter
    }

    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        var isLoading by rememberRetained { mutableStateOf(true) }
        val currentUser by produceRetainedState<FirebaseUser?>(null) {
            firebaseAuth.userFlow()
                .onStart { isLoading = false }
                .catch { Timber.e(it) }
                .flowOn(dispatcherProvider.default)
                .collect { user -> value = user }
        }
        val user = currentUser
        return when {
            isLoading -> State.Loading
            user != null -> State.LoggedIn(
                name = user.displayName,
                email = user.email,
                image = user.photoUrl,
                eventSink = { event ->
                    when (event) {
                        Event.LoggedIn.OnDeleteAccountClick -> TODO()
                        Event.LoggedIn.OnLogoutClick -> coroutineScope.launch {
                            signOut()
                        }

                        Event.LoggedIn.OnNavBack -> navigator.pop()
                    }
                }
            )

            else -> State.NotLoggedIn { event ->
                when (event) {
                    is Event.NotLoggedIn.OnLoginClick -> coroutineScope.launch {
                        isLoading = true
                        val result = authWithGoogle(event.context)
                        isLoading = false
                        if (result.isFailure) {
                            Timber.e(result.exceptionOrNull(), "Authentication with Google failed")
                        }
                    }

                    Event.NotLoggedIn.OnNavBack -> navigator.pop()
                }
            }
        }
    }

    private suspend fun authWithGoogle(context: Context): Result<Boolean> =
        withContext(dispatcherProvider.default) {
            try {
                val response = credentialManager.getCredential(context, getCredentialRequest)
                val authenticated = handleCredentialResponse(response)
                Result.success(authenticated)
            } catch (ex: GetCredentialException) {
                Timber.e(ex)
                Result.failure(ex)
            } catch (ex: NoCredentialException) {
                Timber.e(ex)
                Result.failure(ex)
            }
        }

    private suspend fun handleCredentialResponse(response: GetCredentialResponse): Boolean {
        return (response.credential as? CustomCredential)?.let { credential ->
            if (credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                    val token = googleIdTokenCredential.idToken
                    val credential = GoogleAuthProvider.getCredential(token, null)

                    firebaseAuth.signInWithCredential(credential).await().user != null

                } catch (e: GoogleIdTokenParsingException) {
                    Timber.e("Received an invalid google id token response - $e")
                    false
                }
            } else {
                Timber.e("Unexpected type of credential - ${credential.type}")
                false
            }
        } ?: run {
            Timber.e("Unexpected type of credential - ${response.credential.type}")
            false
        }
    }

    private suspend fun signOut() {
        withContext(dispatcherProvider.default) {
            firebaseAuth.signOut()

            // When a user signs out, clear the current user credential state from all credential providers.
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
            } catch (e: ClearCredentialException) {
                Timber.e("Couldn't clear user credentials: ${e.localizedMessage}")
            }
        }
    }

}