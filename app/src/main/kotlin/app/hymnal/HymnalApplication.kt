// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal

import android.app.Application
import androidx.work.Configuration
import app.hymnal.di.AppGraph
import dev.zacsweers.metro.createGraphFactory

class HymnalApplication : Application(), Configuration.Provider {

    /** Holder reference for the app graph for [HymnalAppComponentFactory]. */
    val appGraph: AppGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(appGraph.workerFactory).build()
}