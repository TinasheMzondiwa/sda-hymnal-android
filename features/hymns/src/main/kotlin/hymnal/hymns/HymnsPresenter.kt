package hymnal.hymns

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
import dev.zacsweers.metro.Inject
import hymnal.hymns.components.SearchResult
import hymnal.hymns.components.pad.NumberPadBottomSheet
import hymnal.libraries.navigation.HymnsScreen
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.services.content.HymnalContentProvider
import kotlinx.collections.immutable.toImmutableList

@Inject
class HymnsPresenter (
    @Assisted private val navigator: Navigator,
    private val contentProvider: HymnalContentProvider,
    private val hymnsStateProducer: HymnsStateProducer
) : Presenter<State> {
    @Composable
    override fun present(): State {
        var searchQuery by rememberRetained { mutableStateOf("") }
        val hymns by produceRetainedState(emptyList()) {
            contentProvider.hymns().collect { value = it }
        }
        val categories by produceRetainedState(emptyList()) {
            contentProvider.categories().collect { value = it }
        }
        val searchResults by produceRetainedState(emptyList(), searchQuery) {
            contentProvider.search(searchQuery)
                .collect { hymn -> value = hymn.map { SearchResult(it) } }
        }

        var sortType by rememberRetained { mutableStateOf(SortType.TITLE) }
        var selectedCategory by rememberRetained(categories) {
            mutableStateOf(categories.firstOrNull())
        }
        var overlayState by rememberRetained { mutableStateOf<OverlayState?>(null) }

        val filteredHymns = hymnsStateProducer(
            hymns = hymns.toImmutableList(),
            category = selectedCategory,
            sortType = sortType,
        )

        return State(
            sortType = sortType,
            selectedCategory = selectedCategory,
            categories = categories.toImmutableList(),
            hymns = filteredHymns,
            searchResults = searchResults.toImmutableList(),
            overlayState = overlayState,
            eventSink = { event ->
                when (event) {
                    Event.OnSortClicked -> sortType = sortType.next()
                    is Event.OnCategorySelected -> selectedCategory = event.category
                    is Event.OnQueryChanged -> {
                        searchQuery = event.query.trim()
                    }
                    is Event.OnSearchResultClicked -> {
                        navigator.goTo(SingHymnScreen(event.result.index))
                    }
                    is Event.OnHymnClicked -> {
                        navigator.goTo(SingHymnScreen(event.index))
                    }
                    Event.OnNumberPadClicked -> {
                        overlayState = OverlayState.NumberPadSheet(
                            onResult = { result ->
                                overlayState = null
                                when (result) {
                                    is NumberPadBottomSheet.Result.Cancel -> Unit
                                    is NumberPadBottomSheet.Result.Confirm -> {
                                        val hymn = hymns.firstOrNull { it.number == result.number }
                                        if (hymn != null) {
                                            navigator.goTo(SingHymnScreen(hymn.index))
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

    @CircuitInject(HymnsScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): HymnsPresenter
    }
}
