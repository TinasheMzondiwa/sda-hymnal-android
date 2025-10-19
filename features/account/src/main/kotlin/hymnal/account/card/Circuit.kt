// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.account.card

import com.slack.circuit.runtime.CircuitUiState

sealed interface AccountCardState : CircuitUiState {
    data object Loading : AccountCardState
    data class LoggedIn(
        val name: String,
        val email: String,
        val image: String?,
        val eventSink: (AccountCardEvent) -> Unit,
    ) : AccountCardState

    data class NotLoggedIn(
        val eventSink: (AccountCardEvent) -> Unit,
    ) : AccountCardState
}

sealed interface AccountCardEvent {
    data object OnAccountCardClick : AccountCardEvent
}