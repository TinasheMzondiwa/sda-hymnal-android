package hymnal.sing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.services.content.HymnalContentProvider
import hymnal.services.model.Hymn

@Inject
class SingHymnPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: SingHymnScreen,
    private val contentProvider: HymnalContentProvider,
) : Presenter<State> {

    @Composable
    override fun present(): State {
        val _hymn by produceRetainedState<Hymn?>(null) {
            contentProvider.hymn(screen.index)
                .collect { value = it }
        }
        val hymn = _hymn
        return when {
            hymn != null -> State.Content(
                hymn = hymn,
                eventSink = { event ->
                    when (event) {
                        is Event.OnNavBack -> navigator.pop()
                    }
                }
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