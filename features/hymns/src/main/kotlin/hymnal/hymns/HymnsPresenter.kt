// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.hymns.components.filters.FilterItem
import hymnal.libraries.model.Hymnal
import hymnal.libraries.navigation.HymnsScreen
import hymnal.libraries.navigation.SearchScreen
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.libraries.navigation.number.NumberPadBottomSheet
import hymnal.services.content.HymnalContentProvider
import hymnal.services.prefs.HymnalPrefs
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import services.hymnal.firebase.AnalyticsService
import timber.log.Timber

@AssistedInject
class HymnsPresenter (
    @Assisted private val navigator: Navigator,
    private val analyticsService: AnalyticsService,
    private val contentProvider: HymnalContentProvider,
    private val hymnsStateProducer: HymnsStateProducer,
    private val lastOpenedHymnStateProducer: LastOpenedHymnStateProducer,
    private val prefs: HymnalPrefs,
) : Presenter<State> {
    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        val hymnal by produceRetainedState(Hymnal.NewHymnal) {
            prefs.currentHymnal()
                .catch { Timber.e(it) }
                .collect { value = it }
        }
        val hymns by produceRetainedState(emptyList(), hymnal) {
            contentProvider.hymns(hymnal.year)
                .catch { Timber.e(it) }
                .collect { value = it }
        }
        val categories by produceRetainedState(emptyList(), hymnal) {
            contentProvider.categories(hymnal.year)
                .catch { Timber.e(it) }
                .collect { value = it }
        }

        var sortType by rememberRetained { mutableStateOf(SortType.NUMBER) }
        var selectedCategory by rememberRetained(categories) {
            mutableStateOf(categories.firstOrNull())
        }
        val filterItems by rememberFilterItems(hymnal, sortType)
        var overlayState by rememberRetained { mutableStateOf<OverlayState?>(null) }

        val filteredHymns = hymnsStateProducer(
            hymns = hymns.toImmutableList(),
            category = selectedCategory,
            sortType = sortType,
        )

        val lastOpenedHymn = lastOpenedHymnStateProducer(navigator)

        return State(
            sortType = sortType,
            selectedCategory = selectedCategory,
            categories = categories.toImmutableList(),
            filterItems = filterItems,
            hymns = filteredHymns,
            lastOpenedHymn = lastOpenedHymn,
            overlayState = overlayState,
            eventSink = { event ->
                when (event) {
                    Event.OnSearch -> navigator.goTo(SearchScreen)
                    is Event.OnCategorySelected -> selectedCategory = event.category
                    is Event.OnFilterItemClicked -> {
                        overlayState = when (event.item) {
                            is FilterItem.Hymnal -> OverlayState.ChooseHymnalSheet(
                                selected = hymnal,
                                onSelection = {
                                    overlayState = null
                                    coroutineScope.launch { prefs.updateCurrentHymnal(it) }

                                    analyticsService.logEvent(
                                        eventName = "switch_hymnal",
                                        params = mapOf("hymnal" to it.name),
                                    )
                                },
                                onResult = { overlayState = null }
                            )

                            is FilterItem.Sort -> OverlayState.ChooseSortTypeSheet(
                                selected = sortType,
                                hymns = hymnal.hymns,
                                onSelection = {
                                    overlayState = null
                                    sortType = it

                                    analyticsService.logEvent(
                                        eventName = "switch_sort_type",
                                        params = mapOf("sort_type" to it.name),
                                    )
                                },
                                onResult = { overlayState = null }
                            )
                        }
                    }
                    is Event.OnHymnClicked -> {
                        navigator.goTo(
                            SingHymnScreen(
                                index = event.index,
                                source = SingHymnScreen.Source.HOME,
                            )
                        )
                    }
                    Event.OnNumberPadClicked -> {
                        overlayState = OverlayState.NumberPadSheet(
                            hymns = hymnal.hymns,
                            onResult = { result ->
                                overlayState = null
                                when (result) {
                                    is NumberPadBottomSheet.Result.Cancel -> Unit
                                    is NumberPadBottomSheet.Result.Confirm -> {
                                        val hymn = hymns.firstOrNull { it.number == result.number }
                                        if (hymn != null) {
                                            navigator.goTo(
                                                SingHymnScreen(
                                                    index = hymn.index,
                                                    source = SingHymnScreen.Source.NUMBER_PICKER,
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
    }

    @Composable
    private fun rememberFilterItems(hymnal: Hymnal, sortType: SortType) =
        rememberRetained(hymnal, sortType) {
            mutableStateOf(
                persistentListOf(
                    FilterItem.Hymnal(
                        title = hymnal.label,
                        selected = hymnal != Hymnal.NewHymnal,
                    ),
                    FilterItem.Sort(
                        titleRes = sortType.title,
                        selected = sortType != SortType.NUMBER,
                        leadingIcon = sortType.icon,
                    ),
                )
            )
        }

    @CircuitInject(HymnsScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): HymnsPresenter
    }
}
