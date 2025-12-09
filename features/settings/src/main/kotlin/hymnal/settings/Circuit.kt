// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.settings

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState

data class State(
    val openLastViewedHymn: Boolean,
    val eventSink: (Event) -> Unit): CircuitUiState

sealed interface Event : CircuitUiEvent {
    data class OnToggleLastViewedHymn(val enable: Boolean): Event
}