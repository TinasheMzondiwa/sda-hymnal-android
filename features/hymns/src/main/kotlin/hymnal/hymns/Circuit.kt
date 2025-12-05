// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.hymns.components.SearchResult
import hymnal.hymns.components.filters.FilterItem
import hymnal.libraries.model.Hymnal
import hymnal.libraries.navigation.number.NumberPadBottomSheet
import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory
import hymnal.ui.circuit.BottomSheetOverlay
import kotlinx.collections.immutable.ImmutableList
import hymnal.hymns.R as HymnsR
import hymnal.libraries.l10n.R as L10nR

data class State(
    val sortType: SortType,
    val selectedCategory: HymnCategory?,
    val categories: ImmutableList<HymnCategory>,
    val filterItems: ImmutableList<FilterItem>,
    val hymns: ImmutableList<Hymn>,
    val searchResults: ImmutableList<SearchResult>,
    val overlayState: OverlayState?,
    val eventSink: (Event) -> Unit
) : CircuitUiState

sealed interface Event : CircuitUiEvent {
    data class OnCategorySelected(val category: HymnCategory) : Event
    data class OnQueryChanged(val query: String) : Event
    data class OnFilterItemClicked(val item: FilterItem) : Event
    data class OnHymnClicked(val index: String) : Event
    data class OnSearchResultClicked(val result: SearchResult) : Event
    data object OnNumberPadClicked : Event
}

sealed interface OverlayState : CircuitUiState {
    data class NumberPadSheet(
        val hymns: Int,
        val onResult: (NumberPadBottomSheet.Result) -> Unit,
    ) : OverlayState

    data class ChooseHymnalSheet(
        val selected: Hymnal,
        val onSelection: (Hymnal) -> Unit,
        val onResult: (BottomSheetOverlay.Result) -> Unit,
    ) : OverlayState

    data class ChooseSortTypeSheet(
        val selected: SortType,
        val hymns: Int,
        val onSelection: (SortType) -> Unit,
        val onResult: (BottomSheetOverlay.Result) -> Unit,
    ) : OverlayState
}

enum class SortType(
    @param:StringRes val title: Int,
    @param:StringRes val desc: Int,
    @param:DrawableRes val icon: Int
) {
    NUMBER(
        L10nR.string.sort_number,
        L10nR.string.sort_number_desc,
        HymnsR.drawable.ic_sort_123,
    ),
    TITLE(
        L10nR.string.sort_title,
        L10nR.string.sort_title_desc,
        HymnsR.drawable.ic_sort_by_alpha,
    ),
}