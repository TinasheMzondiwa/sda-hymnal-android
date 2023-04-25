package app.hymnal.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.hymnal.ui.home.HomeScreen.Event
import app.hymnal.ui.home.HomeScreen.State
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import hymnal.di.AppScope

class HomePresenter @AssistedInject
constructor(
    @Assisted private val navigator: Navigator,
) : Presenter<State> {

    @CircuitInject(HomeScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): HomePresenter
    }

    @Composable
    override fun present(): State {
        println("NAV: $navigator")

        var appRoute by remember { mutableStateOf(AppRoute.Hymnal) }

        val sink: (Event) -> Unit = { event ->
            when (event) {
                is Event.OnNav -> appRoute = event.destination
            }
        }

        return when (appRoute) {
            AppRoute.Hymnal -> State.Hymns(
                hymns = listOf(Hymn("1. Jesus Saves"), Hymn("1. Welcome Welcome")),
                currentRoute = appRoute,
                eventSink = sink
            )

            AppRoute.Collections -> State.Collections(
                collections = emptyList(),
                currentRoute = appRoute,
                eventSink = sink
            )

            AppRoute.TopicalIndex -> State.TopicalIndex(appRoute, sink)
            AppRoute.Settings -> TODO()
            AppRoute.Donate -> TODO()
        }
    }

}