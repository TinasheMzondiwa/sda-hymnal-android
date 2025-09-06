// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.hymns

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import dev.zacsweers.metro.AppScope
import hymnal.collections.components.CollectionColor
import hymnal.libraries.navigation.CollectionHymnsScreen
import hymnal.libraries.navigation.key.CollectionSharedTransitionKey
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.scaffold.HazeScaffold

@OptIn(ExperimentalSharedTransitionApi::class)
@CircuitInject(CollectionHymnsScreen::class, AppScope::class)
@Composable
fun CollectionHymnsScreenUi(state: State, modifier: Modifier = Modifier) {
    SharedElementTransitionScope {
        HazeScaffold(
            modifier = modifier
                .sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            CollectionSharedTransitionKey(
                                id = state.id,
                                type = CollectionSharedTransitionKey.ElementType.Card,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
                .fillMaxSize(),
            topBar = { CollectionHymnsTopAppBar(state) },
            blurTopBar = true,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { contentPadding ->
            LazyColumn(contentPadding = contentPadding) { }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            CollectionHymnsScreenUi(
                state =
                    State(
                        id = "002",
                        title = "Sample Collection",
                        description = "This is a sample collection description.",
                        color = CollectionColor.coralOrange.hex,
                        eventSink = {

                        }
                    ),
            )
        }
    }
}