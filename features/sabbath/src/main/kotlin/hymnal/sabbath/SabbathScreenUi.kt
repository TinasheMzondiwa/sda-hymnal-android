// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.SabbathScreen
import hymnal.sabbath.components.NoLocationContent
import hymnal.sabbath.components.SabbathTopAppBar
import hymnal.sabbath.components.drawRadialGlow
import hymnal.sabbath.components.rememberSabbathColors
import hymnal.services.prefs.model.AppTheme
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.scaffold.HazeScaffold

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@CircuitInject(SabbathScreen::class, AppScope::class)
@Composable
fun SabbathScreenUi(state: State, modifier: Modifier = Modifier) {
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val appTheme = state.theme
    val isDarkTheme by remember(appTheme) {
        derivedStateOf {
            appTheme == AppTheme.DARK || (appTheme == AppTheme.FOLLOW_SYSTEM && isSystemInDarkTheme)
        }
    }
    val colors = rememberSabbathColors(isDark = isDarkTheme)

    val containerColor by animateColorAsState(
        targetValue = if (state is State.SabbathInfo) colors.bg else MaterialTheme.colorScheme.background,
    )
    val contentColor by animateColorAsState(
        targetValue = if (state is State.SabbathInfo) colors.text else contentColorFor(
            containerColor
        ),
    )

    HazeScaffold(
        modifier = modifier
            .fillMaxSize()
            .keepScreenOn()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { SabbathTopAppBar(scrollBehavior = scrollBehavior) },
        blurTopBar = true,
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = containerColor,
        contentColor = contentColor,
    ) { contentPadding ->

        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn(animationSpec = tween(300))
                    .togetherWith(fadeOut(animationSpec = tween(300)))
            },
            label = "content",
        ) { targetState ->
            when (targetState) {
                is State.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingIndicator()
                    }
                }

                is State.NoLocation -> {
                    NoLocationContent(
                        state = targetState,
                        modifier = Modifier
                            .padding(contentPadding),
                    )
                }

                is State.SabbathInfo -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(420.dp)
                                .align(Alignment.Center)
                                .drawRadialGlow(color = colors.accent.copy(alpha = 0.12f))
                        )

                        LazyColumn(
                            modifier = Modifier,
                            contentPadding = contentPadding,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(targetState.items, key = { it.id }) { item ->
                                item.Content(
                                    colors = colors,
                                    modifier = Modifier.animateItem(),
                                )
                            }

                            item(key = "spacer") { Spacer(Modifier.height(48.dp)) }

                            item(key = "spacer-system") {
                                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
                            }
                        }
                    }
                }
            }

        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        SabbathScreenUi(State.Loading(AppTheme.FOLLOW_SYSTEM))
    }
}