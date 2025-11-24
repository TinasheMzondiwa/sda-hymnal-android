// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.ui.home

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/** A functional interface to check if Google Play Services is available.*/
fun interface PlayServicesChecker {
    operator fun invoke(): Boolean
}

@ContributesBinding(AppScope::class)
@Inject
class PlayServicesCheckerImpl(
    private val context: Context
) : PlayServicesChecker {
    override fun invoke(): Boolean {
        return GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }
}
