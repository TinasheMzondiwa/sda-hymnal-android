// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slack.circuit.overlay.OverlayEffect
import hymnal.libraries.navigation.number.NumberPadBottomSheet
import hymnal.sing.BottomBarOverlayState
import hymnal.sing.BottomBarState
import hymnal.sing.components.tune.CombinedIconButton
import hymnal.sing.components.tune.PlaybackState
import hymnal.sing.components.tune.playbackStateOrIdle
import hymnal.sing.components.tune.progressOrZero
import hymnal.sing.components.tune.rememberTunePlayer
import hymnal.ui.extensions.plus
import hymnal.ui.haptics.AppHapticFeedback
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.size.HymnalDimens
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SingBottomAppBar(
    state: BottomBarState,
    modifier: Modifier = Modifier,
    scrollBehavior: BottomAppBarScrollBehavior? = null,
) {
    val iconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
    val textButtonColors = ButtonDefaults.textButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
    val hapticFeedback = LocalAppHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val horizontalPadding = HymnalDimens.horizontalPadding()
    val contentPadding = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal
    ).asPaddingValues().plus(layoutDirection, start = horizontalPadding, end = horizontalPadding)

    FlexibleBottomAppBar(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        scrollBehavior = scrollBehavior,
    ) {

        AnimatedVisibility(visible = !state.tuneIndex.isNullOrEmpty()) {
            PlaybackButton(
                state = state,
                hapticFeedback = hapticFeedback,
                iconButtonColors = iconButtonColors,
                modifier = Modifier
            )
        }

        HymnNavigationButton(
            state = state,
            hapticFeedback = hapticFeedback,
            iconButtonColors = iconButtonColors,
            textButtonColors = textButtonColors,
            modifier = Modifier.weight(1f)
        )
    }

    OverlayContent(state.overlayState)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PlaybackButton(
    state: BottomBarState,
    hapticFeedback: AppHapticFeedback,
    iconButtonColors: IconButtonColors,
    modifier: Modifier = Modifier
) {
    val player = rememberTunePlayer(state.number)
    val playbackState by player.playbackStateOrIdle.collectAsStateWithLifecycle()
    val playbackProgress by player.progressOrZero.collectAsStateWithLifecycle()
    val isPlaying by remember { derivedStateOf { playbackState == PlaybackState.ON_PLAY } }

    val animatedProgress by
    animateFloatAsState(
        targetValue = playbackProgress.percent,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    val trackColor by animateColorAsState(
        targetValue = if (playbackProgress.percent > 0f) {
            ProgressIndicatorDefaults.circularDeterminateTrackColor
        } else {
            Color.Transparent
        }
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CombinedIconButton(
            onClick = {
                hapticFeedback.performClick()
                state.tuneIndex?.let { player?.playPause(it) }
            },
            onLongClick = {
                hapticFeedback.performLongPress()
                player?.stopMedia()
            },
            modifier = Modifier,
            enabled = state.isPlayEnabled,
            showTooltip = state.showTuneToolTip,
            colors = iconButtonColors,
        ) {
            AnimatedContent(
                targetState = isPlaying,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "playPauseIconAnimation"
            ) { targetIsPlaying ->
                Icon(
                    imageVector = if (targetIsPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(L10nR.string.content_play_pause),
                    modifier = Modifier
                )
            }
        }
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(50.dp),
            trackColor = trackColor,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HymnNavigationButton(
    state: BottomBarState,
    hapticFeedback: AppHapticFeedback,
    iconButtonColors: IconButtonColors,
    textButtonColors: ButtonColors,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(48.dp)
            )
    ) {
        IconButton(
            onClick = {
                hapticFeedback.performClick()
                state.eventSink(BottomBarState.Event.OnPreviousHymn)
            },
            enabled = state.previousEnabled,
            colors = iconButtonColors,
        ) {
            Icon(Icons.Rounded.ChevronLeft, null)
        }

        TextButton(
            onClick = {
                hapticFeedback.performClick()
                state.eventSink(BottomBarState.Event.OnGoToHymn)
            },
            modifier = Modifier.weight(1f),
            colors = textButtonColors,
        ) {
            Text(
                text = stringResource(state.titleLabelRes, state.number),
                style = MaterialTheme.typography.titleMediumEmphasized.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        IconButton(
            onClick = {
                hapticFeedback.performClick()
                state.eventSink(BottomBarState.Event.OnNextHymn)
            },
            enabled = state.nextEnabled,
            colors = iconButtonColors,
        ) {
            Icon(Icons.Rounded.ChevronRight, null)
        }
    }
}

@Composable
private fun OverlayContent(state: BottomBarOverlayState?) {
    OverlayEffect(state) {
        when (state) {
            is BottomBarOverlayState.NumberPadSheet -> state.onResult(
                show(NumberPadBottomSheet(state.hymns))
            )

            null -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            SingBottomAppBar(
                state = BottomBarState(
                    tuneIndex = "123",
                    isPlayEnabled = true,
                    showTuneToolTip = false,
                    number = 123,
                    previousEnabled = true,
                    nextEnabled = false,
                    titleLabelRes = L10nR.string.hymn_number,
                    overlayState = null,
                    eventSink = {}
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PreviewNoTune() {
    HymnalTheme {
        Surface {
            SingBottomAppBar(
                state = BottomBarState(
                    tuneIndex = null,
                    isPlayEnabled = false,
                    showTuneToolTip = false,
                    number = 100,
                    previousEnabled = true,
                    nextEnabled = false,
                    titleLabelRes = L10nR.string.chorus_number,
                    overlayState = null,
                    eventSink = {}
                )
            )
        }
    }
}