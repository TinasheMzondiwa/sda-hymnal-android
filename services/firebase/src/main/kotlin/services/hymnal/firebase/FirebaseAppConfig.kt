// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package services.hymnal.firebase

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

interface FirebaseAppConfig {
    val syncHymnsEnabled: Boolean
}

@ContributesBinding(AppScope::class)
@Inject
class FirebaseAppConfigImpl(
) : FirebaseAppConfig {
    private val remoteConfig = Firebase.remoteConfig.also {
        it.fetchAndActivate()
    }
    override val syncHymnsEnabled: Boolean
        get() = remoteConfig.getBoolean(KEY_SYNC_HYMNS_ENABLED)

    private companion object {
        const val KEY_SYNC_HYMNS_ENABLED = "sync_hymns_enabled"
    }
}