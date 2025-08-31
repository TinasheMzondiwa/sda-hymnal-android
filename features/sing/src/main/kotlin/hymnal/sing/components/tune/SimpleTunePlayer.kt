/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.sing.components.tune

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleStartEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min

/**
 * Plays Hymn Tunes using [MediaPlayer].
 *
 * @property context The [Context] for accessing assets.
 */
@Immutable
internal class SimpleTunePlayer(
    private val context: Context
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _progress = MutableStateFlow(PlaybackProgress(0, 0))
    val progress: StateFlow<PlaybackProgress> = _progress.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    fun play(number: Int) {
        stopMedia() // release any prior instance

        try {
            val hymn = number.toString().padStart(3, '0')
            val afd = context.assets.openFd("$FOLDER/$hymn$EXTENSION")

            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

                setOnPreparedListener {
                    // Initialize duration now that we know it.
                    val dur = duration.coerceAtLeast(0)
                    _progress.value = PlaybackProgress(0, dur)

                    start()
                    _playbackState.value = PlaybackState.ON_PLAY
                    startProgressTicker()
                }

                setOnCompletionListener {
                    // Snap to end and stop.
                    val dur = duration.coerceAtLeast(0)
                    _progress.value = PlaybackProgress(dur, dur)
                    _playbackState.value = PlaybackState.ON_COMPLETE
                    stopProgressTicker()
                    // Optionally keep player for seek/replay; here we release:
                    stopMedia()
                }

                setOnErrorListener { _, what, extra ->
                    Timber.e("MediaPlayer error what=$what extra=$extra")
                    _playbackState.value = PlaybackState.ERROR
                    stopProgressTicker()
                    stopMedia()
                    true
                }

                prepare() // synchronous;
            }
            afd.close()
        } catch (ex: Exception) {
            Timber.e(ex)
            _playbackState.value = PlaybackState.ERROR
            stopProgressTicker()
            stopMedia()
        }
    }

    fun stopMedia() {
        try {
            mediaPlayer?.run {
                if (isPlaying) stop()
                reset()
                release()
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        } finally {
            mediaPlayer = null
            stopProgressTicker()
            _playbackState.value = PlaybackState.ON_STOP
            _progress.value = PlaybackProgress(0, 0)
        }
    }

    /** Seek to [positionMs] clamped to [0, duration]. */
    fun seekTo(positionMs: Int) {
        mediaPlayer?.let { mp ->
            val dur = max(0, mp.duration)
            val clamped = min(max(0, positionMs), dur)
            mp.seekTo(clamped)
            _progress.value = PlaybackProgress(clamped, dur)
        }
    }

    /** Toggle pause/resume without changing the current media source. */
    fun playPause(number: Int) {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                pause()
            } else {
                resume()
            }
        } ?: run {
            play(number)
        }
    }

    /** Pause if playing (keeps position). */
    fun pause() {
        try {
            mediaPlayer?.takeIf { it.isPlaying }?.apply {
                pause()
                _playbackState.value = PlaybackState.ON_PAUSE
            }
        } finally {
            // Stop the ticker while paused.
            stopProgressTicker()
        }
    }

    /** Resume if prepared/paused (keeps position). */
    fun resume() {
        mediaPlayer?.let { mp ->
            try {
                if (!mp.isPlaying) {
                    mp.start()
                    _playbackState.value = PlaybackState.ON_PLAY
                    startProgressTicker()
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                _playbackState.value = PlaybackState.ERROR
            }
        }
    }

    /** Internal: emit progress while playing. */
    private fun startProgressTicker(intervalMs: Long = 100L) {
        stopProgressTicker()
        progressJob = scope.launch {
            while (isActive && mediaPlayer != null) {
                val mp = mediaPlayer ?: break
                val dur = max(0, runCatching { mp.duration }.getOrDefault(0))
                val pos = max(0, runCatching { mp.currentPosition }.getOrDefault(0))
                _progress.value = PlaybackProgress(pos, dur)

                // If playback has stopped unexpectedly, break out.
                if (!mp.isPlaying) break

                delay(intervalMs)
            }
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun onPause(owner: LifecycleOwner) {
        stopMedia()
        super.onPause(owner)
    }

    companion object {
        private const val FOLDER = "midis"
        private const val EXTENSION = ".mid"
    }
}

internal val SimpleTunePlayer?.playbackStateOrIdle: StateFlow<PlaybackState>
    get() = this?.playbackState ?: MutableStateFlow(PlaybackState.IDLE).asStateFlow()

internal val SimpleTunePlayer?.progressOrZero: StateFlow<PlaybackProgress>
    get() = this?.progress ?: MutableStateFlow(PlaybackProgress(0, 0)).asStateFlow()

internal enum class PlaybackState { IDLE, ON_PAUSE, ON_PLAY, ON_STOP, ON_COMPLETE, ERROR }

internal data class PlaybackProgress(
    val positionMs: Int,
    val durationMs: Int
) {
    val percent: Float =
        if (durationMs > 0) positionMs.toFloat() / durationMs else 0f
}

@Composable
internal fun rememberTunePlayer(number: Int): SimpleTunePlayer? {
    val context = LocalContext.current
    var player by remember { mutableStateOf<SimpleTunePlayer?>(null) }

    LifecycleStartEffect(number) {
        player = initializePlayer(context)
        onStopOrDispose {
            player?.apply { stopMedia() }
            player = null
        }
    }
    return player
}

private fun initializePlayer(context: Context): SimpleTunePlayer {
    return SimpleTunePlayer(context)
}