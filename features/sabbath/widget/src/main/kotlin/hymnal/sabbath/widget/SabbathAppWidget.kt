// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import dev.zacsweers.metro.createGraphFactory
import hymnal.sabbath.widget.data.WidgetSabbathInfo
import hymnal.sabbath.widget.data.WidgetState
import hymnal.sabbath.widget.theme.HymnalGlanceTheme
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SabbathAppWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val appGraph = createGraphFactory<GlanceAppGraph.Factory>().create(context)
        val repository = appGraph.repository

        provideContent {
            val state by repository.state().collectAsState(WidgetState.Loading)

            HymnalGlanceTheme {
                SabbathAppWidgetContent(state)
            }
        }
    }


    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        super.providePreview(context, widgetCategory)
        val sabbathDay = ZonedDateTime.now().with(DayOfWeek.FRIDAY).withHour(18)
            .withMinute(45)

        provideContent {
            HymnalGlanceTheme {
                SabbathAppWidgetContent(
                    state = WidgetState.Data(
                        sabbathInfo = WidgetSabbathInfo(
                            location = "Springfield, IL",
                            label = "Sabbath starts",
                            dayLabel = sabbathDay
                                .format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                            time = sabbathDay.toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("h:mm a"))
                        )
                    )
                )
            }
        }
    }

}