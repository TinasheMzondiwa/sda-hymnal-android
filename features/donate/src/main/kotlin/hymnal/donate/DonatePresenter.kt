// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.navigation.DonateScreen

@AssistedInject
class DonatePresenter(
    @Assisted private val navigator: Navigator
) : Presenter<State> {

    @CircuitInject(DonateScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): DonatePresenter
    }

    @Composable
    override fun present(): State {
        var selectedTier by rememberRetained { mutableStateOf<DonationTier?>(null) }

        return State(
            tiers = tiers,
            selectedTier = selectedTier,
            eventSink = { event ->
                when (event) {
                    State.Event.OnClose -> navigator.pop()
                    State.Event.OnEnterCustomAmount -> Unit
                    is State.Event.SelectTier -> selectedTier = event.tier
                }
            }
        )
    }
}