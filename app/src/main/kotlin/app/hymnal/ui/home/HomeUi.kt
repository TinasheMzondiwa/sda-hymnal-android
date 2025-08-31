// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import app.hymnal.ui.home.HomeScreen.Event
import app.hymnal.ui.home.HomeScreen.State
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitContent
import dev.zacsweers.metro.AppScope
import hymnal.ui.widget.scaffold.HazeNavigationSuiteScaffold

@CircuitInject(HomeScreen::class, AppScope::class)
@Composable
fun HomeUi(state: State, modifier: Modifier = Modifier) {
    HazeNavigationSuiteScaffold(
        navigationSuiteItems = {
            state.routes.forEach { model ->
                val selected = state.currentRoute == model
                item(
                    selected = selected,
                    onClick = {
                        state.eventSink(Event.OnNav(model))
                    },
                    icon = {
                        Icon(
                            painter = painterResource(model.icon.run {
                                if (selected) filledIcon else icon
                            }),
                            contentDescription = stringResource(model.title),
                        )
                    },
                    label = {
                        Text(stringResource(model.title))
                    },
                )
            }
        },
        modifier = modifier,
    ) {
        AnimatedContent(
            targetState = state.currentRoute.screen(),
            transitionSpec = {
                fadeIn(animationSpec = tween(300))
                    .togetherWith(fadeOut(animationSpec = tween(300)))
            },
            label = "content",
        ) { screen ->
            CircuitContent(
                screen = screen,
                modifier = Modifier.fillMaxSize(),
                onNavEvent = { state.eventSink(Event.OnNavEvent(it)) },
            )
        }
    }
}