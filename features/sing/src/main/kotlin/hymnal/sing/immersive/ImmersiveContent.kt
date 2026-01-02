// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.immersive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.services.playback.LocalTunePlayer
import hymnal.services.playback.TuneItem
import hymnal.sing.immersive.components.ImmersiveTopAppBar
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.scaffold.HazeScaffold
import kotlinx.collections.immutable.persistentListOf
import hymnal.sing.immersive.ImmersiveContentScreen.State as UiState

@CircuitInject(ImmersiveContentScreen::class, AppScope::class)
@Composable
fun ImmersiveContent(state: UiState, modifier: Modifier = Modifier) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    val player = LocalTunePlayer.current

    HazeScaffold(
        modifier = modifier,
        topBar = {
            AnimatedVisibility(
                visible = state.showControls,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ImmersiveTopAppBar(
                    state = state.topBarState,
                    player = player,
                )
            }
        },
        blurTopBar = true,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        val pagerState = rememberPagerState { state.pages.size }

        // Reset the pager to the first page whenever the list of pages changes
        LaunchedEffect(state.pages) {
            pagerState.animateScrollToPage(0)
        }

        Box(modifier = Modifier.fillMaxSize()) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 32.dp,
            ) {
                PageContent(
                    page = state.pages[it],
                    modifier = Modifier,
                )
            }

            AnimatedVisibility(
                visible = state.showControls,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .safeContentPadding(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color by animateColorAsState(
                            targetValue = if (pagerState.currentPage == iteration) {
                                contentColor
                            } else {
                                contentColor.copy(alpha = 0.3f)
                            }
                        )

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageContent(page: ContentPage, modifier: Modifier = Modifier) {
    val text = remember(page) {
        buildAnnotatedString {
            page.lines.forEachIndexed { index, line ->
                if (index > 0) {
                    append("\n")
                }

                if (index == 0) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(line)
                    }
                } else {
                    append(line)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .safeContentPadding(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            lineHeight = 48.sp,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 18.sp,
                maxFontSize = 40.sp,
                stepSize = 2.sp
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        ImmersiveContent(
            state = UiState(
                showControls = true,
                topBarState = TopBarState(
                    number = 1,
                    tune = TuneItem("123", 123, "Grace", "SDA Hymnal"),
                    isPlayEnabled = true,
                    overlayState = null,
                    eventSink = {},
                ),
                pages = persistentListOf(
                    ContentPage(
                        lines = persistentListOf(
                            "1 - Amazing Grace",
                            "Amazing grace! How sweet the sound",
                            "That saved a wretch like me!",
                            "I once was lost, but now am found;",
                            "Was blind, but now I see."
                        )
                    ),
                    ContentPage(
                        lines = persistentListOf(
                            "2",
                            "'Twas grace that taught my heart to fear,",
                            "And grace my fears relieved;",
                            "How precious did that grace appear",
                            "The hour I first believed!"
                        )
                    ),
                    ContentPage(
                        lines = persistentListOf(
                            "Chorus",
                            "When we've been there ten thousand years,",
                            "Bright shining as the sun,",
                            "We've no less days to sing God's praise",
                            "Than when we'd first begun."
                        )
                    )
                ),
            )
        )
    }
}
