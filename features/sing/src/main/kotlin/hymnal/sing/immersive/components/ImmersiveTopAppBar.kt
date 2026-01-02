// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.immersive.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slack.circuit.overlay.OverlayEffect
import hymnal.libraries.navigation.number.NumberPadBottomSheet
import hymnal.services.playback.PlaybackState
import hymnal.services.playback.TunePlayer
import hymnal.services.playback.playbackStateOrIdle
import hymnal.services.playback.progressOrZero
import hymnal.sing.components.tune.CombinedIconButton
import hymnal.sing.immersive.TopBarOverlayState
import hymnal.sing.immersive.TopBarState
import hymnal.ui.haptics.AppHapticFeedback
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.libraries.l10n.R as L10nR
import hymnal.sing.immersive.Event as UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImmersiveTopAppBar(
    state: TopBarState,
    player: TunePlayer?,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalAppHapticFeedback.current
    val iconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )

    TopAppBar(
        title = { Text(text = "${state.number}") },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = {
                    hapticFeedback.performClick()
                    state.eventSink(UiEvent.OnNavBack)
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = null,
                )
            }
        },
        actions = {
            AnimatedVisibility(visible = state.tune != null) {
                PlaybackButton(
                    state = state,
                    player = player,
                    hapticFeedback = hapticFeedback,
                    iconButtonColors = iconButtonColors,
                    modifier = Modifier
                )
            }

            IconButton(
                onClick = {
                    hapticFeedback.performClick()
                    state.eventSink(UiEvent.OnGoToHymn)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Dialpad,
                    contentDescription = stringResource(L10nR.string.content_description_dial_pad)
                )
            }

            Spacer(Modifier.width(48.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        )
    )

    OverlayContent(state.overlayState)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PlaybackButton(
    state: TopBarState,
    player: TunePlayer?,
    hapticFeedback: AppHapticFeedback,
    iconButtonColors: IconButtonColors,
    modifier: Modifier = Modifier
) {
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
                state.tune?.let { player?.playPause(it)  }
            },
            onLongClick = {
                hapticFeedback.performLongPress()
                player?.stop()
            },
            modifier = Modifier,
            enabled = state.isPlayEnabled,
            showTooltip = false,
            colors = iconButtonColors,
        ) {
            AnimatedContent(
                targetState = isPlaying,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "playPauseIconAnimation"
            ) { tuneIsPlaying ->
                Icon(
                    imageVector = if (tuneIsPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
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

@Composable
private fun OverlayContent(state: TopBarOverlayState?) {
    OverlayEffect(state) {
        when (state) {
            is TopBarOverlayState.NumberPadSheet -> state.onResult(
                show(NumberPadBottomSheet(state.hymns))
            )

            null -> Unit
        }
    }
}