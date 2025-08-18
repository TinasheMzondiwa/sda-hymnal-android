package hymnal.sing

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.services.model.Hymn

sealed interface State : CircuitUiState {
    data object Loading: State

    data class Content(
        val hymn: Hymn,
        val eventSink: (Event) -> Unit
    ) : State
}

sealed interface Event : CircuitUiEvent {
    /** Navigation icon is clicked. */
    data object OnNavBack : Event
}