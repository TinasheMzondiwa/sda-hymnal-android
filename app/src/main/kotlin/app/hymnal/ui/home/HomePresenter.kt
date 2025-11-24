// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

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
import dev.zacsweers.metro.AssistedInject
import kotlinx.collections.immutable.toImmutableList

@AssistedInject
class HomePresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: HomeScreen,
    private val playServicesChecker: PlayServicesChecker,
) : Presenter<State> {

    @Composable
    override fun present(): State {
        var currentRoute by rememberRetained { mutableStateOf(screen.route) }
        val routes = rememberRetained {
            buildList {
                add(HomeRoute.Hymns)
                add(HomeRoute.Collections)
                if (playServicesChecker()) {
                    add(HomeRoute.Sabbath)
                }
                add(HomeRoute.More)
            }.toImmutableList()
        }

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
        fun create(navigator: Navigator, screen: HomeScreen): HomePresenter
    }

}