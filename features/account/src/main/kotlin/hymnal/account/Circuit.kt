// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.account

import android.content.Context
import android.net.Uri
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuitx.overlays.DialogResult

sealed interface State : CircuitUiState {
    data object Loading : State

    data class LoggedIn(
        val name: String?,
        val email: String?,
        val image: Uri?,
        val overlay: Overlay?,
        val eventSink: (Event.LoggedIn) -> Unit,
    ): State

    data class NotLoggedIn(val eventSink: (Event.NotLoggedIn) -> Unit) : State
}

sealed interface Event : CircuitUiEvent {
    sealed interface LoggedIn : Event {
        data object OnNavBack : LoggedIn
        data object OnLogoutClick : LoggedIn
        data object OnDeleteAccountClick : LoggedIn
    }

    sealed interface NotLoggedIn : Event {
        data object OnNavBack : NotLoggedIn
        data class OnLoginClick(val context: Context) : NotLoggedIn
    }
}

sealed interface Overlay : CircuitUiState {
    data class ConfirmDeleteAccount(val resultSink: (DialogResult) -> Unit) : Overlay
}