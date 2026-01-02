// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.playback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PlaybackState { IDLE, ON_PAUSE, ON_PLAY, ON_STOP, ON_COMPLETE, ERROR }

val TunePlayer?.playbackStateOrIdle: StateFlow<PlaybackState>
    get() = this?.playbackState ?: MutableStateFlow(PlaybackState.IDLE).asStateFlow()

val TunePlayer?.progressOrZero: StateFlow<PlaybackProgress>
    get() = this?.progress ?: MutableStateFlow(PlaybackProgress(0, 0)).asStateFlow()

data class PlaybackProgress(
    val positionMs: Int,
    val durationMs: Int
) {
    val percent: Float =
        if (durationMs > 0) positionMs.toFloat() / durationMs else 0f
}