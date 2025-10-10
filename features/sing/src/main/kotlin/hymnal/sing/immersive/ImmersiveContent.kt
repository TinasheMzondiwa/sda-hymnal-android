// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.immersive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import hymnal.ui.theme.HymnalTheme
import kotlinx.collections.immutable.persistentListOf
import hymnal.sing.immersive.ImmersiveContentScreen.State as UiState

@CircuitInject(ImmersiveContentScreen::class, AppScope::class)
@Composable
fun ImmersiveContent(state: UiState, modifier: Modifier = Modifier) {
    Scaffold (
        containerColor = Color.Black,
        contentColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { contentPadding ->
        HorizontalPager(
            state = rememberPagerState { state.pages.size },
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            pageSpacing = 32.dp,
        ) {
            val page = state.pages[it]

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
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
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
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        ImmersiveContent(
            state = UiState(
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
                )
            )
        )
    }
}