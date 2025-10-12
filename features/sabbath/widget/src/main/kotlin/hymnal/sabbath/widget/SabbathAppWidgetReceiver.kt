// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dev.zacsweers.metro.createGraphFactory
import hymnal.libraries.coroutines.ioScopable
import hymnal.sabbath.widget.data.SabbathUpdateWidgetWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SabbathAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SabbathAppWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val appGraph = createGraphFactory<GlanceAppGraph.Factory>().create(context)
        val scope = ioScopable(appGraph.dispatcherProvider).scope
        scope.launch {
            SabbathUpdateWidgetWork.schedule(context)
        }
    }
}