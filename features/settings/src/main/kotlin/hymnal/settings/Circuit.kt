// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.settings

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.services.prefs.model.AppTheme

data class State(
    val openLastViewedHymn: Boolean,
    val theme: AppTheme,
    val overlay: SettingsOverlay?,
    val eventSink: (Event) -> Unit,
) : CircuitUiState

sealed interface Event : CircuitUiEvent {
    data class OnToggleLastViewedHymn(val enable: Boolean): Event
    data object OnConfigureTheme : Event
}

sealed interface SettingsOverlay : CircuitUiState {
    data class Theme(
        val theme: AppTheme,
        val onResult: (ThemeOptionsOverlay.Result) -> Unit,
    ) : SettingsOverlay
}