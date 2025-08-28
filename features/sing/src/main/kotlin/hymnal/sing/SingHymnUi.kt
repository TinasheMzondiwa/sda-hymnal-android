package hymnal.sing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.libraries.navigation.key.HymnSharedTransitionKey
import hymnal.sing.components.BottomSheetOverlay
import hymnal.sing.components.SingBottomAppBar
import hymnal.sing.components.SingTopAppBar
import hymnal.sing.components.hymnInfo
import hymnal.sing.components.hymnLyrics
import hymnal.ui.extensions.copy
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.scaffold.HazeScaffold

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@CircuitInject(SingHymnScreen::class, AppScope::class)
@Composable
fun SingHymnUi(state: State, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    val listState: LazyListState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val bottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

    SharedElementTransitionScope {
        val showBottomBar by remember { derivedStateOf { !isTransitionActive } }

        HazeScaffold(
            modifier = modifier
                .sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            HymnSharedTransitionKey(
                                id = (state as? State.Content)?.hymn?.index ?: "card",
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
                    State.Loading -> Unit
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
                            ),
                        ) {
                            SingBottomAppBar(
                                state = state.bottomBarState,
                                modifier = Modifier.skipToLookaheadSize(),
                                scrollBehavior = bottomAppBarScrollBehavior,
                            )
                        }
                    }

                    State.Loading -> Unit
                }
            },
            blurTopBar = true,
            blurBottomBar = true,
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection),
                state = listState,
                contentPadding = contentPadding.copy(
                    layoutDirection = layoutDirection,
                    start = contentPadding.calculateStartPadding(layoutDirection) + 16.dp,
                    top = contentPadding.calculateTopPadding() + 16.dp,
                    end = contentPadding.calculateEndPadding(layoutDirection) + 16.dp,
                    bottom = 0.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                when (state) {
                    is State.Content -> {
                        hymnInfo(
                            hymn = state.hymn,
                            textStyle = state.textStyle,
                        )
                        hymnLyrics(
                            hymnIndex = state.hymn.index,
                            lyrics = state.hymn.lyrics,
                            textStyle = state.textStyle,
                        )
                    }
                    State.Loading -> Unit
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

@Composable
private fun Overlay(state: SingOverlayState?) {
    OverlayEffect(state) {
        when (val overlayState = state) {
            is SingOverlayState.BottomSheet -> {
                state.onResult(
                    show(
                        overlay = BottomSheetOverlay(skipPartiallyExpanded = overlayState.skipPartiallyExpanded) {
                            ContentWithOverlays {
                                CircuitContent(screen = overlayState.screen)
                            }
                        }
                    )
                )
            }
            null -> Unit
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        SingHymnUi(state = State.Loading)
    }
}