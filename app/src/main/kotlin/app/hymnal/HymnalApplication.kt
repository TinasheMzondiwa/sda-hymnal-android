// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal

import android.app.Application
import androidx.work.Configuration
import app.hymnal.di.AppGraph
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class HymnalApplication : Application(), MetroApplication, Configuration.Provider {

    /** Holder reference for the app graph for [HymnalAppComponentFactory]. */
    val appGraph: AppGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(appGraph.workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        appGraph.firebaseAppCheck()
        appGraph.sabbathWidgetHelper.refreshPreview()
        appGraph.remoteConfigService.fetchAndActivate()
    }
}