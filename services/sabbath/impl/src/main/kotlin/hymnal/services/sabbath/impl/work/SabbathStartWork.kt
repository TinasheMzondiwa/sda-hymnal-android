// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl.work

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import libraries.hymnal.di.MetroWorkerFactory
import libraries.hymnal.di.WorkerKey
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import hymnal.libraries.l10n.R as L10nR
import hymnal.services.sabbath.impl.R as SabbathR

@AssistedInject
class SabbathStartWork(context: Context, @Assisted params: WorkerParameters) :
    CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        return if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showNotification()
            Result.success()
        } else {
            // Permission not granted, cannot show notification
            Result.failure()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification() {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        createChannel(notificationManager)
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(SabbathR.drawable.ic_stat_sda)
            .setContentTitle(applicationContext.getString(L10nR.string.sabbath_notification_title))
            .setContentTitle(applicationContext.getString(L10nR.string.sabbath_notification_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createChannel(notificationManager: NotificationManagerCompat) {
        val channelName = applicationContext.getString(L10nR.string.sabbath_notification_channel)
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.description = channelName
        notificationChannel.setShowBadge(true)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @WorkerKey(SabbathStartWork::class)
    @ContributesIntoMap(
        AppScope::class,
        binding = binding<MetroWorkerFactory.WorkerInstanceFactory<*>>(),
    )
    @AssistedFactory
    abstract class Factory : MetroWorkerFactory.WorkerInstanceFactory<SabbathStartWork>

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "sabbath_notifications"
        private const val NOTIFICATION_ID = 18026

        /**
         * Schedule the Sabbath start notification work.
         */
        fun schedule(workManager: WorkManager, sabbathStart: ZonedDateTime) {
            val now = ZonedDateTime.now(sabbathStart.zone)
            val delay = Duration.between(now, sabbathStart).toMillis()

            if (delay <= 0) return // date is in the past, skip scheduling

            val workRequest = OneTimeWorkRequestBuilder<SabbathStartWork>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("sabbath_schedule")
                .build()

            workManager
                .enqueueUniqueWork("sabbath_schedule", ExistingWorkPolicy.REPLACE, workRequest)
        }
    }
}