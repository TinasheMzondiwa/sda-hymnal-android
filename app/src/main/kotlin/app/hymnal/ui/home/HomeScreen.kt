// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.ui.home

import android.os.Parcelable
import com.slack.circuit.foundation.NavEvent
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
data object HomeScreen: Screen, Parcelable {
    data class State(
        val currentRoute: HomeRoute,
        val routes: ImmutableList<HomeRoute>,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data class OnNav(val destination: HomeRoute) : Event
        data class OnNavEvent(val navEvent: NavEvent) : Event
    }
}