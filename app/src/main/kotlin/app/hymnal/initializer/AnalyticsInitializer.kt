// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.initializer

import android.content.Context
import androidx.startup.Initializer
import app.hymnal.BuildConfig
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.crashlytics.setCustomKeys

class AnalyticsInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(Firebase.analytics) {
            setUserProperty("version_code", BuildConfig.VERSION_CODE.toString())
        }

        Firebase.crashlytics.setCustomKeys {
            key("version_code", BuildConfig.VERSION_CODE.toString())
            key(
                "play_services_enabled", "${
                    GoogleApiAvailability.getInstance()
                        .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
                }"
            )
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
