/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package app.hymnal

import android.app.Application
import app.hymnal.di.AppGraph
import dev.zacsweers.metro.createGraphFactory

class HymnalApplication : Application() {

    /** Holder reference for the app graph for [HymnalAppComponentFactory]. */
    val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }
}