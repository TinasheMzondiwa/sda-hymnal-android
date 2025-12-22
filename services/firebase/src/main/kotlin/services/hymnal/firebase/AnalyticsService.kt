// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package services.hymnal.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

interface AnalyticsService {
    fun logEvent(eventName: String, params: Map<String, Any>? = null)
}

@ContributesBinding(scope = AppScope::class, binding<AnalyticsService>())
@Inject
class FirebaseAnalyticsService(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsService {

    override fun logEvent(eventName: String, params: Map<String, Any>?) {
        val bundle = Bundle()

        params?.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Float -> bundle.putFloat(key, value)
                else -> bundle.putString(key, value.toString()) // Fallback
            }
        }

        firebaseAnalytics.logEvent(eventName, bundle)
    }


}