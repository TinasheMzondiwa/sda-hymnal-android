package app.hymnal.ui.home

import android.os.Parcelable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

data class Hymn(val title: String)
data class Collection(val title: String)

@Parcelize
object HomeScreen : Screen, Parcelable {

    sealed interface State : CircuitUiState {

        val currentRoute: AppRoute
        val eventSink: (Event) -> Unit

        data class Hymns(
            val hymns: List<Hymn>,
            override val currentRoute: AppRoute = AppRoute.Hymnal,
            override val eventSink: (Event) -> Unit
        ) : State

        data class Collections(
            val collections: List<Collection>,
            override val currentRoute: AppRoute = AppRoute.Collections,
            override val eventSink: (Event) -> Unit
        ) : State

        data class TopicalIndex(
            override val currentRoute: AppRoute = AppRoute.TopicalIndex,
            override val eventSink: (Event) -> Unit
        ): State
    }

    sealed interface Event : CircuitUiEvent {
        data class OnNav(val destination: AppRoute) : Event
    }
}