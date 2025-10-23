// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.initializer

import android.content.Context
import androidx.startup.Initializer
import app.hymnal.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

class AnalyticsInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(Firebase.analytics) {
            setUserProperty("version_code", BuildConfig.VERSION_CODE.toString())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
