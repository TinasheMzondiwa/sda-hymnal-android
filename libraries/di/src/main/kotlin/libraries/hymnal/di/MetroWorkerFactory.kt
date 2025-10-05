// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package libraries.hymnal.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.reflect.KClass

@ContributesBinding(AppScope::class)
@Inject
class MetroWorkerFactory(
    private val assistedWorkerFactories:
    Map<KClass<out ListenableWorker>, AssistedWorkerFactory<out ListenableWorker>>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        val factory = assistedWorkerFactories[Class.forName(workerClassName).kotlin]
        return factory?.createWorker(appContext, workerParameters)
    }
}

fun interface AssistedWorkerFactory<T : ListenableWorker> {
    fun createWorker(appContext: Context, workerParams: WorkerParameters): T
}