package hymnal.hymns

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.hymns.components.SearchResult
import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory
import kotlinx.collections.immutable.ImmutableList
import hymnal.libraries.l10n.R as L10nR

data class State(
    val sortType: SortType,
    val selectedCategory: HymnCategory?,
    val categories: ImmutableList<HymnCategory>,
    val hymns: ImmutableList<Hymn>,
    val searchResults: ImmutableList<SearchResult>,
    val eventSink: (Event) -> Unit
) : CircuitUiState

sealed interface Event : CircuitUiEvent {
    data object OnSortClicked : Event
    data class OnCategorySelected(val category: HymnCategory) : Event
    data class OnQueryChanged(val query: String) : Event
}

enum class SortType(@param:StringRes val title: Int, val icon: ImageVector) {
    NUMBER(L10nR.string.sort_number, Icons.Rounded.FormatListNumbered),
    TITLE(L10nR.string.sort_title, Icons.Rounded.SortByAlpha),
}

fun SortType.next(): SortType = when (this) {
    SortType.NUMBER -> SortType.TITLE
    SortType.TITLE -> SortType.NUMBER
}