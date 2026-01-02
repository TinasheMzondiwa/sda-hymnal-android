// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.playback.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import hymnal.libraries.coroutines.DispatcherProvider

@DependencyGraph(AppScope::class)
interface ServiceAppGraph {
    val dispatcherProvider: DispatcherProvider

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides context: Context): ServiceAppGraph
    }
}