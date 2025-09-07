// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.hymns

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.services.model.Hymn
import kotlinx.collections.immutable.ImmutableList

sealed interface State: CircuitUiState {
    val id: String

    data class Loading(override val id: String) : State

    data class Content(
        override val id: String,
        val title: String,
        val description: String?,
        val color: String,
        val hymns: ImmutableList<Hymn>,
        val eventSink: (Event) -> Unit,
    ) : State
}

sealed interface Event : CircuitUiEvent {
    data object OnNavBack : Event
    data class OnHymnClicked(val index: String) : Event
    data object OnDeleteCollectionClicked : Event
}