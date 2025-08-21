package hymnal.sing

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.sing.components.HymnContent

sealed interface State : CircuitUiState {
    data object Loading: State

    data class Content(
        val hymn: HymnContent,
        val bottomBarState: BottomBarState,
        val eventSink: (Event) -> Unit
    ) : State
}

data class BottomBarState(
    val number: Int,
    val isPlaying: Boolean,
    val isPlayEnabled: Boolean,
    val previousEnabled: Boolean,
    val nextEnabled: Boolean,
    val eventSink: (Event) -> Unit,
) : CircuitUiState {
    sealed interface Event : CircuitUiEvent {
        data object OnPlayPause : Event
        data object OnPreviousHymn : Event
        data object OnNextHymn : Event
        data object OnGoToHymn : Event
    }
}

sealed interface Event : CircuitUiEvent {
    /** Navigation icon is clicked. */
    data object OnNavBack : Event
}