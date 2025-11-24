// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.state

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@Stable
interface CurrentLocationStateProducer {
    suspend operator fun invoke(): LocationResult
}

sealed interface LocationResult {
    data class Granted(val location: Location) : LocationResult
    data object NotGranted : LocationResult
    data object NotAvailable : LocationResult
}

@ContributesBinding(AppScope::class)
@Inject
class CurrentLocationStateProducerImpl(private val context: Context) : CurrentLocationStateProducer {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun invoke(): LocationResult {
        if (!hasLocationPermissions(context)) {
            return LocationResult.NotGranted
        }
        val location = getLastLocationIfApiAvailable().await()
        return if (location != null) {
            LocationResult.Granted(location)
        } else {
            LocationResult.NotAvailable
        }
    }

    private fun hasLocationPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLastLocationIfApiAvailable(): Task<Location?> {
        val client = fusedLocationClient
        return GoogleApiAvailability.getInstance()
            .checkApiAvailability(client)
            .onSuccessTask { _ -> client.lastLocation }
            .addOnFailureListener { e -> Timber.e( "Location unavailable.", e)}
    }
}