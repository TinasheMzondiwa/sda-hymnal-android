// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath

import com.slack.circuit.runtime.CircuitUiState

sealed interface State : CircuitUiState {
    object Loading : State
    data class NoLocation(
        val eventSink: (Event.NoLocation) -> Unit
    ) : State

    data class SabbathInfo(
        val location: String,
    ) : State
}

sealed interface Event {
    sealed interface NoLocation : Event {
        data object OnLocationGranted : NoLocation
        data object OnLocationDenied : NoLocation
    }
}