// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.di

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.work.WorkManager
import app.hymnal.BuildConfig
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import hymnal.libraries.model.HymnalAppConfig
import hymnal.services.content.HymnalContentSyncProvider
import hymnal.services.sabbath.api.SabbathWidgetHelper
import libraries.hymnal.di.MetroWorkerFactory
import kotlin.reflect.KClass
import app.hymnal.R

@DependencyGraph(AppScope::class)
interface AppGraph {

    /**
     * A multibinding map of activity classes to their providers accessible for
     * [MetroAppComponentFactory].
     */
    @Multibinds
    val activityProviders: Map<KClass<out Activity>, Provider<Activity>>

    val workerFactory: MetroWorkerFactory
    val sabbathWidgetHelper: SabbathWidgetHelper

    @Provides
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    @SingleIn(AppScope::class)
    fun providesWorkManager(application: Context): WorkManager {
        return WorkManager.getInstance(application)
    }


    @Provides
    @SingleIn(AppScope::class)
    fun provideCircuit(
        presenterFactories: Set<Presenter.Factory>,
        uiFactories: Set<Ui.Factory>,
    ): Circuit {
        return Circuit.Builder()
            .addPresenterFactories(presenterFactories)
            .addUiFactories(uiFactories)
            .build()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideAppConfig(context: Context): HymnalAppConfig = HymnalAppConfig(
        version = BuildConfig.VERSION_NAME,
        buildNumber = BuildConfig.VERSION_CODE,
        isDebug = BuildConfig.DEBUG,
        webClientId = context.getString(R.string.default_web_client_id),
    )

    val contentSyncProvider: HymnalContentSyncProvider

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}