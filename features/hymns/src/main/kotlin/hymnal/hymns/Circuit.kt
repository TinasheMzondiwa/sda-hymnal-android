package hymnal.hymns

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory

sealed interface State : CircuitUiState {
    data object Loading : State
    data class Hymns(
        val selectedCategory: HymnCategory?,
        val categories: List<HymnCategory>,
        val hymns: List<Hymn>,
        val eventSink: (Event) -> Unit
    ) : State
}

sealed interface Event : CircuitUiEvent {
    data class OnCategorySelected(val category: HymnCategory) : Event
}