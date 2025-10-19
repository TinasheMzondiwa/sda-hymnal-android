// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.account.card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.navigation.AccountCardScreen
import kotlinx.coroutines.delay

@AssistedInject
class AccountCardPresenter : Presenter<AccountCardState> {

    @CircuitInject(AccountCardScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(): AccountCardPresenter
    }

    @Composable
    override fun present(): AccountCardState {
        var state by rememberRetained {
            mutableStateOf<AccountCardState>(AccountCardState.Loading)
        }

        LaunchedEffect(Unit) {
            delay(1500)
            state = AccountCardState.NotLoggedIn(eventSink = {})
        }

        return state
    }
}