// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.hymns

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState

data class State(
    val id: String,
    val title: String,
    val description: String?,
    val color: String?,
    val eventSink: (Event) -> Unit,
): CircuitUiState

sealed interface Event : CircuitUiEvent {
    data object OnNavBack : Event
}