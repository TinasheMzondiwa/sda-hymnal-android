/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.hymns.sabbath

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.services.model.Hymn
import kotlinx.collections.immutable.ImmutableList

data class State(
    val hymns: ImmutableList<Hymn>,
    val eventSink: (Event) -> Unit,
) : CircuitUiState

sealed interface Event : CircuitUiEvent {
    data object OnNavBackClicked : Event
    data class OnHymnClicked(val index: String) : Event
}