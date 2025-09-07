// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.hymns

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.collections.components.CollectionColor
import hymnal.libraries.navigation.CollectionHymnsScreen
import hymnal.ui.extensions.copy
import hymnal.ui.haptics.AppHapticFeedback
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.scaffold.HazeScaffold
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@CircuitInject(CollectionHymnsScreen::class, AppScope::class)
@Composable
fun CollectionHymnsScreenUi(state: State, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    val hapticFeedback = LocalAppHapticFeedback.current
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

        HazeScaffold(
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { CollectionHymnsTopAppBar(state = state, scrollBehavior = scrollBehavior) },
            blurTopBar = true,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { contentPadding ->
            LazyColumn(
                contentPadding = contentPadding.copy(
                    layoutDirection = layoutDirection,
                    top = contentPadding.calculateTopPadding() + 12.dp,
                    start = contentPadding.calculateStartPadding(layoutDirection) + 16.dp,
                    end = contentPadding.calculateEndPadding(layoutDirection) + 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listContent(state, hapticFeedback)
            }
        }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
private fun LazyListScope.listContent(state: State, hapticFeedback: AppHapticFeedback) {
    when (state) {
        is State.Content -> {
            item("description") {
                state.description?.let {
                        CollectionDescription(
                            description = it,
                            color = state.color,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                }
            }
            items(state.hymns, key = { it.index }) { hymn ->
                HymnItem(
                    hymn = hymn,
                    modifier = Modifier.animateItem(),
                    onClick = {
                        hapticFeedback.performClick()
                        state.eventSink(Event.OnHymnClicked(hymn.index))
                    },
                )
            }
        }

        is State.Loading -> {
            item("loading") {
                Box(
                    Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }
        }
    }

    // Fab clearance space
    item("fab-spacer") {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(72.dp)
        )
    }

    item("insets") { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)) }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
        HymnalTheme {
            CollectionHymnsScreenUi(
                state =
                    State.Content(
                        id = "collection_1",
                        title = "Sample Collection",
                        description = "This is a sample collection description.",
                        color = CollectionColor.coralOrange.hex,
                        hymns = persistentListOf(previewHymn),
                        eventSink = {
                        }
                    ),
            )
        }
}