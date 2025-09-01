// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import hymnal.services.model.HymnsCollection
import hymnal.ui.circuit.BottomSheetOverlay
import kotlinx.collections.immutable.ImmutableList

sealed interface State : CircuitUiState {
    val overlayState: CollectionOverlayState?
    val eventSink: (Event) -> Unit

    data class Loading(
        override val overlayState: CollectionOverlayState? = null,
        override val eventSink: (Event) -> Unit) : State

    data class Empty(
        override val overlayState: CollectionOverlayState? = null,
        override val eventSink: (Event) -> Unit) : State

    data class Content(
        val collections: ImmutableList<HymnsCollection>,
        override val overlayState: CollectionOverlayState?,
        override val eventSink: (Event) -> Unit,
    ) : State
}

sealed interface Event: CircuitUiEvent {
    data class OnCollectionClicked(val collection: HymnsCollection) : Event
    data object OnAddCollectionClicked : Event
}

sealed interface CollectionOverlayState : CircuitUiState {
    data class BottomSheet(
        val screen: Screen,
        val skipPartiallyExpanded: Boolean = false,
        val onResult: (BottomSheetOverlay.Result) -> Unit,
    ): CollectionOverlayState
}

