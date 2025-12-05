// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.libraries.navigation.key.HymnSharedTransitionKey
import hymnal.sing.components.SingBottomAppBar
import hymnal.sing.components.SingTopAppBar
import hymnal.sing.components.hymnInfo
import hymnal.sing.components.hymnLyrics
import hymnal.ui.circuit.BottomSheetOverlay
import hymnal.ui.extensions.plus
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.size.HymnalDimens
import hymnal.ui.widget.scaffold.HazeScaffold

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@CircuitInject(SingHymnScreen::class, AppScope::class)
@Composable
fun SingHymnUi(state: State, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    val listState: LazyListState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val bottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    val horizontalPadding = HymnalDimens.horizontalPadding()

    SharedElementTransitionScope {
        val showBottomBar by remember { derivedStateOf { !isTransitionActive } }

        HazeScaffold(
            modifier = modifier
                .sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            HymnSharedTransitionKey(
                                id = state.index,
                                type = HymnSharedTransitionKey.ElementType.Card,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .keepScreenOn(),
            topBar = {
                when (state) {
                    is State.Content -> SingTopAppBar(
                        state = state.topBarState,
                        scrollBehavior = scrollBehavior,
                    )
                    is State.Loading -> Unit
                }
            },
            bottomBar = {
                when (state) {
                    is State.Content -> {
                        AnimatedVisibility(
                            visible = showBottomBar,
                            modifier = Modifier.fillMaxWidth(),
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 150,
                                    delayMillis = 50
                                )
                            ) + slideInVertically { it },
                        ) {
                            SingBottomAppBar(
                                state = state.bottomBarState,
                                modifier = Modifier.skipToLookaheadSize(),
                                scrollBehavior = bottomAppBarScrollBehavior,
                            )
                        }
                    }
                    is State.Loading -> Unit
                }
            },
            blurTopBar = true,
            blurBottomBar = true,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection),
                state = listState,
                contentPadding = contentPadding.plus(
                    layoutDirection = layoutDirection,
                    start = horizontalPadding,
                    top = 16.dp,
                    end = horizontalPadding,
                    bottom = 0.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                when (state) {
                    is State.Content -> {
                        hymnInfo(
                            hymn = state.hymn,
                            textStyle = state.textStyle,
                            onAuthorLinkClicked = { state.eventSink(Event.OnAuthorLinkClick(it)) },
                        )
                        hymnLyrics(
                            hymnIndex = state.hymn.index,
                            lyrics = state.hymn.lyrics,
                            textStyle = state.textStyle,
                        )
                    }
                    is State.Loading -> item("loading") {
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

                item {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                }

                item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)) }
            }
        }
    }

    Overlay((state as? State.Content)?.overlayState)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Overlay(state: SingOverlayState?) {
    OverlayEffect(state) {
        when (state) {
            is SingOverlayState.BottomSheet -> {
                state.onResult(
                    show(
                        overlay = BottomSheetOverlay(skipPartiallyExpanded = state.skipPartiallyExpanded) {
                            val backstack = rememberSaveableBackStack(state.screen)
                            val navigator = rememberCircuitNavigator(backstack)

                            SharedElementTransitionLayout {
                                NavigableCircuitContent(
                                    navigator = navigator,
                                    backStack = backstack,
                                    decoratorFactory =
                                        remember(navigator) {
                                            GestureNavigationDecorationFactory(onBackInvoked = navigator::pop)
                                        },
                                )
                            }
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
private fun Preview() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            SingHymnUi(state = State.Loading("001"))
        }
    }
}