// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Binder
import android.os.IBinder
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import dev.zacsweers.metro.createGraphFactory
import hymnal.services.playback.di.ServiceAppGraph
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import hymnal.libraries.l10n.R as L10nR
import hymnal.services.playback.R as PlaybackR

class TuneService : Service() {

    private val binder = LocalBinder()
    lateinit var tunePlayer: TunePlayer

    private val serviceAppGraph: ServiceAppGraph by lazy {
        createGraphFactory<ServiceAppGraph.Factory>().create(applicationContext)
    }
    private val serviceScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + serviceAppGraph.dispatcherProvider.main)
    }

    private val exceptionLogger = CoroutineExceptionHandler { _, e -> Timber.e(e) }

    override fun onCreate() {
        super.onCreate()
        tunePlayer = TunePlayerImpl(this, serviceAppGraph.dispatcherProvider)

        serviceScope.launch(exceptionLogger) {
            combine(tunePlayer.playbackState, tunePlayer.nowPlaying) { state, nowPlaying ->
                state to nowPlaying
            }
                .catch { Timber.e(it) }
                .collect { (state, nowPlaying) ->
                    when (state) {
                        PlaybackState.IDLE -> Unit
                        PlaybackState.ON_PLAY -> {
                            // PROMOTE to Foreground Service (Non-dismissible notification)
                            val notification =
                                createNotification(isPlaying = true, item = nowPlaying)
                            startForeground(NOTIFICATION_ID, notification)
                        }

                        PlaybackState.ON_PAUSE,
                        PlaybackState.ON_COMPLETE,
                        PlaybackState.ERROR -> {
                            stopForeground(STOP_FOREGROUND_DETACH)

                            // We still want to update the notification UI (e.g. to show "Pause" icon)
                            // even though we are no longer "Foreground"
                            val notification =
                                createNotification(isPlaying = false, item = nowPlaying)

                            try {
                                startForeground(NOTIFICATION_ID, notification)
                            } catch (e: Exception) {
                                // Fallback for edge cases where service isn't allowed to start foreground
                                Timber.w(e, "Failed to startForeground during pause update")
                                getSystemService(NotificationManager::class.java)
                                    .notify(NOTIFICATION_ID, notification)
                            }

                            stopForeground(STOP_FOREGROUND_DETACH)
                        }

                        PlaybackState.ON_STOP -> {
                            stopForeground(STOP_FOREGROUND_REMOVE)
                            getSystemService(NotificationManager::class.java)
                                .cancel(NOTIFICATION_ID)
                        }
                    }
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle Notification Button Clicks
        when (intent?.action) {
            ACTION_PAUSE -> tunePlayer.pause()
            ACTION_PLAY -> {
                // Check if player lost its state (Service restart)
                val item =
                    IntentCompat.getParcelableExtra(intent, ARG_TUNE_ITEM, TuneItem::class.java)
                if (item != null) {
                    tunePlayer.playPause(item)
                } else {
                    tunePlayer.resume() // Try resuming anyway
                }
            }
            ACTION_STOP -> {
                tunePlayer.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        // Clean up the MediaPlayer
        (tunePlayer as? TunePlayerImpl)?.release()
        super.onDestroy()
    }

    inner class LocalBinder : Binder() {
        fun getService(): TuneService = this@TuneService
    }

    private fun createNotification(isPlaying: Boolean, item: TuneItem?): Notification {
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Hymn Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(channel)

        val pauseIntent = Intent(this, TuneService::class.java).apply {
            action = ACTION_PAUSE
            item?.let { putExtra(ARG_TUNE_ITEM, it) }
        }
        val playIntent = Intent(this, TuneService::class.java).apply {
            action = ACTION_PLAY
            item?.let { putExtra(ARG_TUNE_ITEM, it) }
        }
        val stopIntent = Intent(this, TuneService::class.java).apply { action = ACTION_STOP }

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val pausePendingIntent =
            PendingIntent.getService(this, 100, pauseIntent, flags)
        val playPendingIntent =
            PendingIntent.getForegroundService(this, 101, playIntent, flags)
        val stopPendingIntent =
            PendingIntent.getService(this, 102, stopIntent, flags)

        val builder = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("${item?.number ?: ""} ${item?.title.orEmpty()}")
            .setContentText(item?.hymnal.orEmpty())
            .setSmallIcon(PlaybackR.drawable.ic_stat_sda)
            .setOngoing(isPlaying)
            .setStyle(
                Notification.MediaStyle()
                    .setShowActionsInCompactView(0, 1)
            )

        if (isPlaying) {
            builder.addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(applicationContext, PlaybackR.drawable.ic_pause),
                    "Pause",
                    pausePendingIntent,
                ).build()
            )
        } else {
            builder.addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(applicationContext, PlaybackR.drawable.ic_play_arrow),
                    "Play",
                    playPendingIntent,
                ).build()
            )
        }

        // Always add a Close/Stop button
        builder.addAction(
            Notification.Action.Builder(
                Icon.createWithResource(applicationContext, PlaybackR.drawable.ic_close),
                "Stop",
                stopPendingIntent
            ).build()
        )

        // Open Activity on tap
        item?.index?.let { builder.setContentIntent(contentIntent(it)) }

        return builder.build()
    }

    private fun contentIntent(index: String): PendingIntent? {
        val intent =
            applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
                ?.apply {
                    data =
                        "${applicationContext.getString(L10nR.string.app_scheme)}//hymn?index=$index".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }

        return intent?.let {
            PendingIntent.getActivity(applicationContext, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
    }

    private companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "hymn_playback_channel"
        const val ARG_TUNE_ITEM = "arg:tune_item"

        // Define action strings
        const val ACTION_PLAY = "com.tinashe.sdah.ACTION_PLAY"
        const val ACTION_PAUSE = "com.tinashe.sdah.ACTION_PAUSE"
        const val ACTION_STOP = "com.tinashe.sdah.ACTION_STOP"
    }
}