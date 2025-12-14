// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.model.Hymnal
import hymnal.libraries.navigation.SearchScreen
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.services.content.HymnalContentProvider
import hymnal.services.model.Hymn
import hymnal.services.prefs.HymnalPrefs
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import timber.log.Timber

@AssistedInject
class SearchPresenter(
    @Assisted private val navigator: Navigator,
    private val contentProvider: HymnalContentProvider,
    private val prefs: HymnalPrefs,
) : Presenter<State> {

    @Composable
    override fun present(): State {
        val hymnal by produceRetainedState(Hymnal.NewHymnal) {
            prefs.currentHymnal()
                .catch { Timber.e(it) }
                .collect { value = it }
        }
        var query by rememberRetained { mutableStateOf("") }
        val recentHymns by rememberRecentHymns()
        var selectedHymnal by rememberRetained(hymnal) { mutableStateOf(hymnal) }
        // local flag to prevent showing empty results during search debounce
        var fetching by rememberRetained { mutableStateOf(false) }

        val searchResults by produceRetainedState(persistentListOf(), query) {
            if (query.isBlank()) {
                value = persistentListOf()
                return@produceRetainedState
            }

            fetching = true

            delay(300)

            contentProvider.search(query)
                .catch { Timber.e(it) }
                .collect { hymns ->
                    value = hymns.toImmutableList()
                    fetching = false
                }
        }

        val resultsFilters by rememberResultsFilters(hymnal, selectedHymnal, searchResults)

        val eventSink: (Event) -> Unit = { event ->
            when (event) {
                Event.OnNavigateUp -> navigator.pop()
                Event.OnClearQuery -> query = ""
                is Event.OnFilterChange -> {
                    Hymnal.fromYear(event.filter.year)?.let { selectedHymnal = it }
                }

                is Event.OnQueryChange -> {
                    query = event.query.trimStart()
                }

                is Event.OnHymnClick -> navigator.goTo(SingHymnScreen(event.hymn.index))
            }
        }

        return when {
            query.isEmpty() -> State.RecentHymns(
                hymns = recentHymns,
                query = query,
                eventSink = eventSink,
            )

            searchResults.isNotEmpty() || fetching -> State.SearchResults(
                filters = resultsFilters,
                hymns = searchResults
                    .filter { it.year == selectedHymnal.year }
                    .toImmutableList(),
                query = query,
                eventSink = eventSink,
            )

            else -> State.Empty(
                query = query,
                eventSink = eventSink,
            )
        }
    }

    @Composable
    private fun rememberRecentHymns() = produceRetainedState(persistentListOf()) {
        contentProvider.recentHymns()
            .catch { Timber.e(it) }
            .collect { hymns -> value = hymns.toImmutableList() }
    }

    @Composable
    private fun rememberResultsFilters(
        hymnal: Hymnal,
        selectedHymnal: Hymnal,
        results: ImmutableList<Hymn>
    ): androidx.compose.runtime.State<ImmutableList<FilterItem>> =
        produceRetainedState<ImmutableList<FilterItem>>(
            persistentListOf(),
            hymnal,
            selectedHymnal,
            results
        ) {
            val grouped = results.groupBy { it.year }

            value = buildList {
                if (grouped.keys.size > 1) {
                    add(
                        FilterItem(
                            selected = hymnal == selectedHymnal,
                            label = "${hymnal.label()} (${grouped[hymnal.year]?.size ?: 0})",
                            year = hymnal.year
                        )
                    )
                    Hymnal.entries.forEach {
                        if (it != hymnal) {
                            val hymns = grouped[it.year]
                            if (!hymns.isNullOrEmpty()) {
                                add(
                                    FilterItem(
                                        selected = selectedHymnal == it,
                                        label = "${it.label()} (${hymns.size})",
                                        year = it.year
                                    )
                                )
                            }
                        }
                    }
                }
            }.toImmutableList()
        }

    private fun Hymnal.label(): String = when (this) {
        Hymnal.OldHymnal -> "Old hymnal"
        Hymnal.NewHymnal -> "New hymnal"
        Hymnal.Choruses -> "Choruses"
    }

    @CircuitInject(SearchScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): SearchPresenter
    }
}