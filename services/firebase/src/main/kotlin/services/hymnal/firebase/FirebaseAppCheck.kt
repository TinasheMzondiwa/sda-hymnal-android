// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package services.hymnal.firebase

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.model.HymnalAppConfig

interface FirebaseAppCheck {
    operator fun invoke()
}

@ContributesBinding(AppScope::class)
@Inject
class FirebaseAppCheckImpl(
    private val appConfig: HymnalAppConfig,
    private val appContext: Context
) : FirebaseAppCheck {
    override fun invoke() {
        Firebase.initialize(context = appContext)
        if (appConfig.isDebug) {
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance(),
            )
        } else {
            Firebase.appCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance(),
            )
        }
    }

}