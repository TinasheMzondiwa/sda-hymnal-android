// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget.helper

import android.appwidget.AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN
import android.content.Context
import android.os.Build
import androidx.collection.intSetOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.coroutines.Scopable
import hymnal.libraries.coroutines.defaultScopable
import hymnal.sabbath.widget.SabbathAppWidget
import hymnal.sabbath.widget.SabbathAppWidgetReceiver
import hymnal.sabbath.widget.data.SabbathUpdateWidgetWork
import hymnal.sabbath.widget.sdk.isAtLeastApi
import hymnal.services.sabbath.api.SabbathWidgetHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber

@ContributesBinding(AppScope::class, binding = binding<SabbathWidgetHelper>())
@Inject
class SabbathWidgetHelperImpl(
    dispatcherProvider: DispatcherProvider,
    private val context: Context,
) : SabbathWidgetHelper, Scopable by defaultScopable(dispatcherProvider) {

    private val glanceManager: GlanceAppWidgetManager by lazy {
        GlanceAppWidgetManager(context)
    }

    private val exceptionLogger = CoroutineExceptionHandler { _, e -> Timber.e(e) }

    override fun refresh() {
        scope.launch(exceptionLogger) {
            if (isAdded()) {
                SabbathAppWidget().updateAll(context)
                SabbathUpdateWidgetWork.schedule(context)
            } else {
                SabbathUpdateWidgetWork.cancel(context)
            }
        }
    }

    override fun refreshPreview() {
        if (isAtLeastApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)) {
            scope.launch(exceptionLogger) {
                glanceManager.setWidgetPreviews(
                    SabbathAppWidgetReceiver::class,
                    intSetOf(WIDGET_CATEGORY_HOME_SCREEN)
                )
            }
        }

        refresh()
    }

    private suspend fun isAdded(): Boolean {
        return glanceManager.getGlanceIds(SabbathAppWidget().javaClass)
            .isNotEmpty()
    }
}