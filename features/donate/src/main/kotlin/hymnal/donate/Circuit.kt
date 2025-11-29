// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class State(
    val tiers: ImmutableList<DonationTier>,
    val selectedTier: DonationTier?,
    val eventSink: (Event) -> Unit,
) : CircuitUiState {
    sealed interface Event : CircuitUiEvent {
        data object OnClose : Event
        data class SelectTier(val tier: DonationTier) : Event
        data object OnEnterCustomAmount : Event
    }
}

@Immutable
data class DonationTier(
    val id: String,
    val formattedPrice: String,
    val isMostPopular: Boolean = false
)
