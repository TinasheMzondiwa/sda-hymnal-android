package hymnal.hymns

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material.icons.rounded._123
import androidx.compose.ui.graphics.vector.ImageVector
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory
import hymnal.libraries.l10n.R as L10nR

sealed interface State : CircuitUiState {
    data object Loading : State
    data class Hymns(
        val sortType: SortType,
        val selectedCategory: HymnCategory?,
        val categories: List<HymnCategory>,
        val hymns: List<Hymn>,
        val eventSink: (Event) -> Unit
    ) : State
}

sealed interface Event : CircuitUiEvent {
    data object OnSortClicked : Event
    data class OnCategorySelected(val category: HymnCategory) : Event
}

enum class SortType(@param:StringRes val title: Int, val icon: ImageVector) {
    NUMBER(L10nR.string.sort_number, Icons.Rounded._123),
    TITLE(L10nR.string.sort_title, Icons.Rounded.SortByAlpha),
}

fun SortType.next(): SortType = when (this) {
    SortType.NUMBER -> SortType.TITLE
    SortType.TITLE -> SortType.NUMBER
}