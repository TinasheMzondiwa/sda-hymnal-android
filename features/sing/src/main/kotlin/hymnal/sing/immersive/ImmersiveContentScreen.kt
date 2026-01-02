// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.immersive

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import hymnal.libraries.navigation.number.NumberPadBottomSheet
import hymnal.services.playback.TuneItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImmersiveContentScreen(val hymnId: String, val showControls: Boolean) : Screen {
    data class State(
        val showControls: Boolean,
        val topBarState: TopBarState,
        val pages: ImmutableList<ContentPage>,
    ) : CircuitUiState
}

data class TopBarState(
    val number: Int,
    val tune: TuneItem?,
    val isPlayEnabled: Boolean,
    val overlayState: TopBarOverlayState?,
    val eventSink: (Event) -> Unit,
): CircuitUiState

sealed interface Event : CircuitUiEvent {
    data object OnNavBack : Event
    data object OnGoToHymn : Event
}

sealed interface TopBarOverlayState : CircuitUiState {
    data class NumberPadSheet(
        val hymns: Int,
        val onResult: (NumberPadBottomSheet.Result) -> Unit,
    ) : TopBarOverlayState
}

@Immutable
data class ContentPage(
    val lines: ImmutableList<String>,
)
