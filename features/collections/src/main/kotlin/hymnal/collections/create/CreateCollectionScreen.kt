// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.create

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import hymnal.collections.components.CollectionColor
import kotlinx.parcelize.Parcelize

@Parcelize
object CreateCollectionScreen : Screen {

    data class State(
        val selectedColor: CollectionColor,
        val saveEnabled: Boolean,
        val eventSink: (Event) -> Unit
    ): CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data class OnTitleChanged(val title: CharSequence) : Event
        data class OnColorSelected(val color: CollectionColor) : Event
        data class SaveClicked(
            val title: CharSequence,
            val description: CharSequence?
        ) : Event
    }
}

