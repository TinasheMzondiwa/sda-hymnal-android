// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget.data

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import hymnal.sabbath.widget.SabbathAppWidget
import libraries.hymnal.di.AssistedWorkerFactory
import libraries.hymnal.di.WorkerKey
import java.util.concurrent.TimeUnit

@AssistedInject
class SabbathUpdateWidgetWork(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    @WorkerKey(SabbathUpdateWidgetWork::class)
    @ContributesIntoMap(
        scope = AppScope::class,
        binding = binding<AssistedWorkerFactory<out ListenableWorker>>(),
    )
    @AssistedFactory
    fun interface Factory : AssistedWorkerFactory<SabbathUpdateWidgetWork>

    override suspend fun doWork(): Result {
        SabbathAppWidget().updateAll(applicationContext)

        return Result.success()
    }

    companion object {
        private const val UNIQUE_NAME = "hymnal.sabbath.widget.data.SabbathUpdateWidgetWork"
        private const val INTERVAL = 1L

        fun schedule(context: Context) {
            val manager = WorkManager.getInstance(context)
            val workRequest = PeriodicWorkRequestBuilder<SabbathUpdateWidgetWork>(
                INTERVAL,
                TimeUnit.HOURS
            ).build()
            manager.enqueueUniquePeriodicWork(
                UNIQUE_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_NAME)
        }
    }
}