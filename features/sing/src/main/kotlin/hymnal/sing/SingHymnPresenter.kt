package hymnal.sing

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
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.services.content.HymnalContentProvider
import hymnal.sing.components.HymnContent
import hymnal.sing.state.BottomBarStateProducer
import hymnal.sing.state.TopBarStateProducer

@Inject
class SingHymnPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: SingHymnScreen,
    private val contentProvider: HymnalContentProvider,
    private val topBarStateProducer: TopBarStateProducer,
    private val bottomBarStateProducer: BottomBarStateProducer,
) : Presenter<State> {

    @Composable
    override fun present(): State {
        var hymnIndex by rememberRetained { mutableStateOf(screen.index) }
        var overlayState by rememberRetained { mutableStateOf<SingOverlayState?>(null) }
        val _hymn by produceRetainedState<HymnContent?>(null, hymnIndex) {
            contentProvider.hymn(hymnIndex)
                .collect { value = it?.let { HymnContent(it) } }
        }
        val hymn = _hymn

        val topBarState = topBarStateProducer(navigator)
        val bottomBarState = bottomBarStateProducer(hymn) { hymnIndex = it }

        return when {
            hymn != null -> State.Content(
                hymn = hymn,
                topBarState = topBarState,
                bottomBarState = bottomBarState,
                overlayState = overlayState ?: topBarState.overlayState,
                eventSink = { event -> }
            )

            else -> State.Loading
        }
    }

    @CircuitInject(SingHymnScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator, screen: SingHymnScreen): SingHymnPresenter
    }
}