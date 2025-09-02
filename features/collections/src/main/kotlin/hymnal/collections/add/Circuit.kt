// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.add

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import kotlinx.collections.immutable.ImmutableList

sealed interface State : CircuitUiState {
    val eventSink: (Event) -> Unit

    data class Loading(override val eventSink: (Event) -> Unit) : State
    data class Empty(override val eventSink: (Event) -> Unit) : State
    data class Choose(
        val collections: ImmutableList<ChooseCollectionSpec>,
        override val eventSink: (Event) -> Unit,
    ) : State
}

sealed interface Event : CircuitUiEvent {
    data object CreateNewCollection : Event
    data class CollectionSelected(val spec: ChooseCollectionSpec) : Event
}