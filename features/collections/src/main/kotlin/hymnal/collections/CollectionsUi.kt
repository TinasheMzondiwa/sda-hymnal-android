// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import dev.zacsweers.metro.AppScope
import hymnal.collections.components.CollectionCard
import hymnal.collections.components.CollectionsTopBar
import hymnal.collections.components.emptyCollections
import hymnal.libraries.navigation.CollectionsScreen
import hymnal.services.model.HymnsCollection
import hymnal.ui.circuit.BottomSheetOverlay
import hymnal.ui.extensions.copy
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.scaffold.HazeScaffold
import kotlinx.collections.immutable.persistentListOf
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(CollectionsScreen::class, AppScope::class)
@Composable
fun CollectionsUi(state: State, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    val hapticFeedback = LocalAppHapticFeedback.current
    val listState: LazyListState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val fabVisible by remember { derivedStateOf { !listState.isScrollInProgress } }

    HazeScaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollectionsTopBar(scrollBehavior = scrollBehavior)
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        hapticFeedback.performClick()
                        state.eventSink(Event.OnAddCollectionClicked)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(L10nR.string.content_description_add_collection)
                    )
                }
            }

        },
        contentWindowInsets = WindowInsets.safeDrawing,
        blurTopBar = true,
    ) { contentPadding ->
        LazyColumn(
            state = listState,
            contentPadding = contentPadding.copy(
                layoutDirection = layoutDirection,
                top = contentPadding.calculateTopPadding() + 12.dp,
            ),
        ) {

            collectionsContent(state)

            // Fab clearance space
            item {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                )
            }

            item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)) }
        }
    }

    Overlay(state.overlayState)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyListScope.collectionsContent(state: State) {
    when (state) {
        is State.Loading ->
            item(key = "loading") {
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
        is State.Empty -> emptyCollections()
        is State.Content -> {
            items(items = state.collections, key = { it.collectionId }) { collection ->
                CollectionCard(
                    collection = collection,
                    modifier = Modifier.animateItem()
                ) {
                    state.eventSink(Event.OnCollectionClicked(collection))
                }
            }
        }
    }
}

@Composable
private fun Overlay(state: CollectionOverlayState?) {
    OverlayEffect(state) {
        when (val overlayState = state) {
            is CollectionOverlayState.BottomSheet -> {
                state.onResult(
                    show(
                        overlay = BottomSheetOverlay(skipPartiallyExpanded = overlayState.skipPartiallyExpanded) {
                            CircuitContent(
                                screen = overlayState.screen,
                                onNavEvent = {
                                    state.onResult(BottomSheetOverlay.Result.Dismissed)
                                }
                            )
                        }
                    )
                )
            }
            null -> Unit
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun CollectionsUiPreview() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            CollectionsUi(State.Content(
                collections = persistentListOf(
                    HymnsCollection(
                        collectionId = "1",
                        title = "Favorites",
                        description = "My favorite hymns",
                        hymns = listOf(),
                        created = 0L,
                        color = "#FF5722"
                    ),
                ),
                overlayState = null
            ) {})
        }
    }
}