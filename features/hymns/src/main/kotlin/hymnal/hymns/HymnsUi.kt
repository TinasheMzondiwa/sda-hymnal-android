// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dialpad
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
import com.slack.circuit.overlay.OverlayEffect
import dev.zacsweers.metro.AppScope
import hymnal.hymns.components.HymnCard
import hymnal.hymns.components.HymnsTopAppBar
import hymnal.hymns.components.previewHymn
import hymnal.libraries.navigation.HymnsScreen
import hymnal.libraries.navigation.number.NumberPadBottomSheet
import hymnal.services.model.HymnCategory
import hymnal.ui.extensions.plus
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.size.HymnalDimens
import hymnal.ui.widget.scaffold.HazeScaffold
import kotlinx.collections.immutable.persistentListOf
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@CircuitInject(HymnsScreen::class, AppScope::class)
@Composable
fun HymnsUi(state: State, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    val hapticFeedback = LocalAppHapticFeedback.current
    val listState: LazyListState = rememberLazyListState()
    val horizontalPadding = HymnalDimens.horizontalPadding()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val fabVisible by remember { derivedStateOf { !listState.isScrollInProgress } }

    HazeScaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { HymnsTopAppBar(state, Modifier, scrollBehavior) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        state.eventSink(Event.OnNumberPadClicked)
                        hapticFeedback.performScreenView()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        Icons.Rounded.Dialpad,
                        contentDescription = stringResource(L10nR.string.content_description_dial_pad)
                    )
                }
            }
        },
        blurTopBar = true,
    ) { contentPadding ->
        LazyColumn(
            state = listState,
            contentPadding = contentPadding.plus(
                layoutDirection = layoutDirection,
                top = 12.dp,
                start = horizontalPadding,
                end = horizontalPadding,
            ),
        ) {
            items(state.hymns, key = { it.index }) { hymn ->
                HymnCard(
                    hymn = hymn,
                    sortType = state.sortType.next(),
                    modifier = Modifier.animateItem(),
                    onClick = {
                        hapticFeedback.performClick()
                        state.eventSink(Event.OnHymnClicked(hymn.index))
                    },
                )
            }

            if (state.hymns.isEmpty()) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }
            }

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

    OverlayContent(state.overlayState)
}

@Composable
private fun OverlayContent(state: OverlayState?) {
    OverlayEffect(state) {
        when (state) {
            is OverlayState.NumberPadSheet -> state.onResult(
                show(NumberPadBottomSheet())
            )
            null -> Unit
        }
    }
}

private val previewCategories = persistentListOf(
    HymnCategory("1", "Category 1", 1, 2),
    HymnCategory("2", "Category 2", 2, 3),
    HymnCategory("2", "Category 3", 3, 4)
)

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        HymnsUi(
            State(
                sortType = SortType.NUMBER,
                selectedCategory = previewCategories.first(),
                categories = previewCategories,
                hymns = persistentListOf(previewHymn, previewHymn.copy(index = "2")),
                searchResults = persistentListOf(),
                overlayState = null,
                eventSink = {},
            )
        )
    }
}