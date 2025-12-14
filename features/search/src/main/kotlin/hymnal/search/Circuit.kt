// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.search

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.services.model.Hymn
import kotlinx.collections.immutable.ImmutableList

sealed interface State : CircuitUiState {
    val query: String
    val eventSink: (Event) -> Unit

    data class RecentHymns(
        val hymns: ImmutableList<Hymn>,
        override val query: String,
        override val eventSink: (Event) -> Unit,
    ) : State

    data class Empty(override val query: String, override val eventSink: (Event) -> Unit) : State

    data class SearchResults(
        val filters: ImmutableList<FilterItem>,
        val hymns: ImmutableList<Hymn>,
        override val query: String,
        override val eventSink: (Event) -> Unit,
    ) : State
}

sealed interface Event : CircuitUiEvent {
    data object OnNavigateUp : Event
    data class OnQueryChange(val query: String) : Event
    data object OnClearQuery : Event
    data class OnFilterChange(val filter: FilterItem) : Event
    data class OnHymnClick(val hymn: Hymn) : Event
}

@Immutable
data class FilterItem(
    val selected: Boolean,
    val label: String,
    val year: String,
)