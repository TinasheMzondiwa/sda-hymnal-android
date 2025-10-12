// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.sabbath.widget.data.WidgetRepository

@DependencyGraph(AppScope::class)
interface GlanceAppGraph {

    val repository: WidgetRepository
    val dispatcherProvider: DispatcherProvider

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides context: Context): GlanceAppGraph
    }
}