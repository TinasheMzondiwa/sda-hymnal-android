// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.playback

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import hymnal.libraries.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

internal class TunePlayerImpl(
    private val context: Context,
    dispatcherProvider: DispatcherProvider,
) : TunePlayer {

    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private var mediaPlayer: MediaPlayer? = null

    // Track the currently loaded index (URI/Path) to support playPause logic
    private var currentMediaId: String? = null

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    override val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _progress = MutableStateFlow(PlaybackProgress(0, 0))
    override val progress: StateFlow<PlaybackProgress> = _progress

    private val _nowPlaying = MutableStateFlow<TuneItem?>(null)
    override val nowPlaying: StateFlow<TuneItem?> = _nowPlaying

    // Audio Focus variables
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var resumeOnFocusGain = false // Remember if we should auto-resume

    init {
        startProgressTicker()
    }

    // ========================================================================
    // Audio Focus Listener
    // ========================================================================
    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        println("ON FOCUS state...$focusChange")
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // We got focus back (e.g., phone call ended)
                if (resumeOnFocusGain) {
                    resumeOnFocusGain = false
                    resume() // Safe resume that updates state
                }
                // Restore volume if we were ducked
                mediaPlayer?.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanent loss (e.g., user started Spotify) -> Stop/Pause
                resumeOnFocusGain = false
                pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Temporary loss (e.g., phone call) -> Pause but remember to resume
                if (_playbackState.value == PlaybackState.ON_PLAY) {
                    resumeOnFocusGain = true
                    pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (_playbackState.value == PlaybackState.ON_PLAY) {
                    mediaPlayer?.setVolume(0.2f, 0.2f)
                }
            }
        }
    }

    // ========================================================================
    // Player Implementation
    // ========================================================================

    override fun play(item: TuneItem) {
        if (requestAudioFocus()) {
            loadAndPlay(item)
        }
        _nowPlaying.value = item
    }

    override fun playPause(item: TuneItem) {
        if (currentMediaId == item.index && mediaPlayer != null) {
            // We are interacting with the currently loaded track
            when (_playbackState.value) {
                PlaybackState.ON_PLAY -> {
                    pause()
                }
                PlaybackState.ON_COMPLETE -> {
                    if (requestAudioFocus()) {
                        // Restart if finished
                        loadAndPlay(item)
                    }
                }
                else -> {
                    resume()
                }
            }
            _nowPlaying.value = item
        } else {
            when (_playbackState.value) {
                PlaybackState.ON_PLAY -> pause()
                else -> {
                    // New track
                    loadAndPlay(item)
                    _nowPlaying.value = item
                }
            }
        }
    }

    override fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            _playbackState.value = PlaybackState.ON_PAUSE
        }
    }

    override fun stop() {
        releaseMediaPlayer()
        _playbackState.value = PlaybackState.ON_STOP
        _nowPlaying.value = null
    }

    override fun resume() {
        if (requestAudioFocus()) {
            // Only resume if we are in a valid state (Paused or Prepared)
            if (_playbackState.value == PlaybackState.ON_PAUSE || _playbackState.value == PlaybackState.IDLE) {
                mediaPlayer?.start()
                _playbackState.value = PlaybackState.ON_PLAY
            }
        }
    }

    private fun loadAndPlay(item: TuneItem) {
        releaseMediaPlayer() // Clean up old player

        try {
            mediaPlayer = MediaPlayer().apply {
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

                // Attach attributes to the player
                setAudioAttributes(attributes)

                setOnCompletionListener {
                    _playbackState.value = PlaybackState.ON_COMPLETE
                    // Ensure progress shows 100% at end
                    _progress.value = PlaybackProgress(duration, duration)
                }

                setOnErrorListener { _, _, _ ->
                    _playbackState.value = PlaybackState.ERROR
                    true // Return true to indicate we handled the error
                }

                val afd = context.assets.openFd("$FOLDER/${item.index}${EXTENSION}")
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
            }

            currentMediaId = item.index
            _playbackState.value = PlaybackState.ON_PLAY

        } catch (e: IOException) {
            Timber.e(e, "Error loading media")
            _playbackState.value = PlaybackState.ERROR
        }
    }

    private fun startProgressTicker() {
        scope.launch {
            while (isActive) {
                if (mediaPlayer?.isPlaying == true) {
                    val current = mediaPlayer?.currentPosition ?: 0
                    val total = mediaPlayer?.duration ?: 0
                    _progress.value = PlaybackProgress(current, total)
                }
                delay(500)
            }
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentMediaId = null
    }

    override fun release() {
        releaseMediaPlayer()
        abandonAudioFocus()
    }

    // ========================================================================
    // Focus Helpers
    // ========================================================================

    private fun requestAudioFocus(): Boolean {
        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(focusChangeListener)
            .build()

        audioFocusRequest = request
        val result = audioManager.requestAudioFocus(request)

        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun abandonAudioFocus() {
        audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
    }

    private companion object {
        const val FOLDER = "midis"
        const val EXTENSION = ".mid"
    }

}