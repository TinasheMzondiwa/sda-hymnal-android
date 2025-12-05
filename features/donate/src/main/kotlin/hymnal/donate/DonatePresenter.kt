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
import hymnal.donate.ui.TierButtonSpec
import hymnal.libraries.navigation.DonateScreen
import kotlinx.collections.immutable.toImmutableList

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
        var selectedTier by rememberRetained { mutableStateOf<TierButtonSpec?>(null) }

        return State(
            tiers = tiers.map {
                it.copy(
                    selected =  it == selectedTier
                )
            }.toImmutableList(),
            selectedTier = selectedTier,
            eventSink = { event ->
                when (event) {
                    Event.OnClose -> navigator.pop()
                    Event.OnEnterCustomAmount -> Unit
                    is Event.SelectTier -> selectedTier = event.tier
                }
            }
        )
    }
}