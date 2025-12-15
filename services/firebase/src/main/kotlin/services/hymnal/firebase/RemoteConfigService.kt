// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package services.hymnal.firebase

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.coroutines.Scopable
import hymnal.libraries.coroutines.ioScopable
import hymnal.libraries.model.HymnalAppConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface RemoteConfigService {
    val syncHymnsEnabled: Boolean

    /**
     * triggers a fetch to update values from the server.
     */
    fun fetchAndActivate()
}

@ContributesBinding(scope = AppScope::class, binding<RemoteConfigService>())
@Inject
class FirebaseRemoteConfigService(
    appConfig: HymnalAppConfig,
    dispatcherProvider: DispatcherProvider,
) : RemoteConfigService, Scopable by ioScopable(dispatcherProvider) {

    private val exceptionLogger = CoroutineExceptionHandler { _, e -> Timber.e(e) }

    init {
        val defaults = mapOf(
            KEY_SYNC_HYMNS_ENABLED to false
        )
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(if (appConfig.isDebug) 0 else 3600)
            .build()

        with(Firebase.remoteConfig) {
            setDefaultsAsync(defaults)
            setConfigSettingsAsync(configSettings)
        }
    }

    override val syncHymnsEnabled: Boolean
        get() = Firebase.remoteConfig.getBoolean(KEY_SYNC_HYMNS_ENABLED)

    override fun fetchAndActivate() {
        scope.launch(exceptionLogger) {
            Firebase.remoteConfig.fetchAndActivate().await()
        }
    }

    private companion object {
        const val KEY_SYNC_HYMNS_ENABLED = "sync_hymns_enabled"
    }
}