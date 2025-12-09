// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.navigation.SettingsScreen
import hymnal.services.prefs.HymnalPrefs
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

@AssistedInject
class SettingsPresenter(
    private val prefs: HymnalPrefs,
) : Presenter<State> {

    @CircuitInject(SettingsScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(): SettingsPresenter
    }

    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        val openLastViewedHymn by produceRetainedState(false) {
            prefs.showLastHymn()
                .catch { Timber.e(it) }
                .collect {
                    value = it
                }
        }
        return State(openLastViewedHymn = openLastViewedHymn) { event ->
            when (event) {
                is Event.OnToggleLastViewedHymn -> {
                    coroutineScope.launch { prefs.setShowLastHymn(event.enable) }
                }
            }
        }
    }
}