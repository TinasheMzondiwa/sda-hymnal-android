// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl.work

import android.content.Context
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
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.services.sabbath.impl.asDateTime
import hymnal.storage.db.dao.SabbathTimesDao
import kotlinx.coroutines.withContext
import libraries.hymnal.di.AssistedWorkerFactory
import libraries.hymnal.di.WorkerKey
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@AssistedInject
class SabbathRoutineWork(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val sabbathTimesDao: SabbathTimesDao,
    private val dispatcherProvider: DispatcherProvider,
    private val workManager: WorkManager,
) : CoroutineWorker(context, params) {

    @WorkerKey(SabbathRoutineWork::class)
    @ContributesIntoMap(
        scope = AppScope::class,
        binding = binding<AssistedWorkerFactory<out ListenableWorker>>(),
    )
    @AssistedFactory
    fun interface Factory : AssistedWorkerFactory<SabbathRoutineWork>

    override suspend fun doWork(): Result {
        val entity = withContext(dispatcherProvider.io) { sabbathTimesDao.get() }
        val sabbathStart = entity?.friday?.asDateTime()

        return if (sabbathStart != null && sabbathStart.isAfter(ZonedDateTime.now(sabbathStart.zone))) {
            SabbathStartWork.schedule(workManager, sabbathStart)
            Result.success()
        } else {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "sabbath_routine_work"
        private val INTERVAL = 24.hours.toJavaDuration()

        fun schedule(workManager: WorkManager) {
            val request = PeriodicWorkRequestBuilder<SabbathRoutineWork>(INTERVAL)
                .clearNextScheduleTimeOverride()
                .build()

            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName = UNIQUE_NAME,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                request = request,
            )
        }
    }
}