package app.hymnal.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.hymnal.ui.home.HomeScreen.Event
import app.hymnal.ui.home.HomeScreen.State
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.onNavEvent
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.toImmutableList

@Inject
class HomePresenter(@Assisted private val navigator: Navigator) : Presenter<State> {

    @Composable
    override fun present(): State {
        var currentRoute by rememberRetained { mutableStateOf(HomeRoute.Hymns) }
        val routes = rememberRetained { HomeRoute.entries.toImmutableList() }

        return State(
            currentRoute = currentRoute,
            routes = routes,
            eventSink = { event ->
                when (event) {
                    is Event.OnNav -> currentRoute = event.destination
                    is Event.OnNavEvent -> navigator.onNavEvent(event.navEvent)
                }
            },
        )
    }

    @CircuitInject(HomeScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): HomePresenter
    }

}