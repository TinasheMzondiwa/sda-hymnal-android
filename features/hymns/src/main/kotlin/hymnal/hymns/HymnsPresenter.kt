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
import hymnal.libraries.navigation.HymnsScreen
import hymnal.services.content.HymnalContentProvider
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

@Inject
class HymnsPresenter (
    @Assisted private val navigator: Navigator,
    private val contentProvider: HymnalContentProvider,
    private val hymnsStateProducer: HymnsStateProducer
) : Presenter<State> {
    @Composable
    override fun present(): State {
        val hymns by produceRetainedState(emptyList()) {
            contentProvider.hymns().collect { value = it }
        }
        val categories by produceRetainedState(emptyList()) {
            contentProvider.categories().collect { value = it }
        }

        var selectedCategory by rememberRetained(categories) { mutableStateOf(categories.firstOrNull()) }

        val filteredHymns = hymnsStateProducer(hymns.toImmutableList(), selectedCategory)

        return when {
            filteredHymns.isEmpty() -> State.Loading
            else -> State.Hymns(
                selectedCategory = selectedCategory,
                categories = categories,
                hymns = filteredHymns,
                eventSink = { event ->
                    when (event) {
                        is Event.OnCategorySelected -> {
                            selectedCategory = event.category
                        }
                    }
                }
            )
        }
    }

    @CircuitInject(HymnsScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): HymnsPresenter
    }
}
