// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import hymnal.libraries.navigation.number.NumberPadBottomSheet
import hymnal.sing.components.HymnContent
import hymnal.sing.components.model.TextStyleSpec
import hymnal.ui.circuit.BottomSheetOverlay

sealed interface State : CircuitUiState {
    val index: String

    data class Loading(override val index: String): State

    data class Content(
        override val index: String = hymn.index,
        val hymn: HymnContent,
        val topBarState: TopBarState,
        val bottomBarState: BottomBarState,
        val overlayState: SingOverlayState?,
        val textStyle: TextStyleSpec,
        val eventSink: (Event) -> Unit
    ) : State
}

sealed interface SingOverlayState : CircuitUiState {
    data class BottomSheet(
        val screen: Screen,
        val skipPartiallyExpanded: Boolean = false,
        val onResult: (BottomSheetOverlay.Result) -> Unit,
    ) : SingOverlayState
}

data class TopBarState(
    val overlayState: SingOverlayState?,
    val eventSink: (Event) -> Unit
) : CircuitUiState {
    sealed interface Event : CircuitUiEvent {
        /** Navigation icon is clicked. */
        data object OnNavBack : Event
        data object OnStyleClick : Event
        data object OnSaveClick : Event
        data object OnFullscreenClick : Event
        data object OnShareClick : Event
    }
}

data class BottomBarState(
    val number: Int,
    val isPlayEnabled: Boolean,
    val showTuneToolTip: Boolean,
    val previousEnabled: Boolean,
    val nextEnabled: Boolean,
    val overlayState: BottomBarOverlayState?,
    val eventSink: (Event) -> Unit,
) : CircuitUiState {
    sealed interface Event : CircuitUiEvent {
        data object OnPreviousHymn : Event
        data object OnNextHymn : Event
        data object OnGoToHymn : Event
    }
}

sealed interface Event : CircuitUiEvent {

}

sealed interface BottomBarOverlayState : CircuitUiState {
    data class NumberPadSheet(
        val onResult: (NumberPadBottomSheet.Result) -> Unit,
    ) : BottomBarOverlayState
}