// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.donate.ui.TierButtonSpec
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class State(
    val tiers: ImmutableList<TierButtonSpec>,
    val selectedTier: TierButtonSpec?,
    val eventSink: (Event) -> Unit,
) : CircuitUiState

sealed interface Event : CircuitUiEvent {
    data object OnClose : Event
    data class SelectTier(val tier: TierButtonSpec) : Event
    data object OnEnterCustomAmount : Event
}
