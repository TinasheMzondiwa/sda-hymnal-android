// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.account.card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.google.firebase.auth.FirebaseAuth
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.navigation.AccountCardScreen
import hymnal.libraries.navigation.AccountScreen
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import services.hymnal.firebase.userFlow
import timber.log.Timber

@AssistedInject
class AccountCardPresenter(
    @Assisted private val navigator: Navigator,
    private val firebaseAuth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider,
) : Presenter<AccountCardState> {

    @CircuitInject(AccountCardScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): AccountCardPresenter
    }

    @Composable
    override fun present(): AccountCardState {
        val eventSink: (AccountCardEvent) -> Unit = { event ->
            when (event) {
                AccountCardEvent.OnAccountCardClick -> navigator.goTo(AccountScreen)
            }
        }

        val state by produceRetainedState<AccountCardState>(AccountCardState.Loading) {
            firebaseAuth.userFlow()
                .catch { Timber.e(it) }
                .flowOn(dispatcherProvider.default)
                .collect { user ->
                    value = if (user != null) {
                        AccountCardState.LoggedIn(
                            name = user.displayName ?: "Anonymous",
                            email = user.email,
                            image = user.photoUrl,
                            eventSink = eventSink,
                        )
                    } else {
                        AccountCardState.NotLoggedIn(eventSink = eventSink)
                    }
                }
        }

        return state
    }
}