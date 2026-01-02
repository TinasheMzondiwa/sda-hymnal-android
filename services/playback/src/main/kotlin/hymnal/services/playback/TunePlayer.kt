// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.playback

import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.StateFlow

@Stable
interface TunePlayer {

    val playbackState: StateFlow<PlaybackState>

    val progress: StateFlow<PlaybackProgress>

    val nowPlaying: StateFlow<TuneItem?>

    /** Stop the current media source and play this media item. */
    fun play(item: TuneItem)

    /** Toggle pause/resume without changing the current media source. */
    fun playPause(item: TuneItem)

    /** Pause if playing (keeps position). */
    fun pause()

    /** Stop if playing or prepared (releases resources). */
    fun stop()

    /** Resume if prepared/paused (keeps position). */
    fun resume()

    fun release()
}

@SuppressLint("ComposeCompositionLocalUsage")
val LocalTunePlayer = staticCompositionLocalOf<TunePlayer?> { null }