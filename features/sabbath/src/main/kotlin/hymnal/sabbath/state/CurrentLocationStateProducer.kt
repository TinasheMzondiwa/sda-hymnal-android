// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.state

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.tasks.await

interface CurrentLocationStateProducer {
    suspend operator fun invoke(): LocationResult
}

sealed interface LocationResult {
    data class Granted(val location: Location) : LocationResult
    object NotGranted : LocationResult
}

@ContributesBinding(AppScope::class)
@Inject
class CurrentLocationStateProducerImpl(private val context: Context) : CurrentLocationStateProducer {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun invoke(): LocationResult {
        return if (hasLocationPermissions(context)) {
            LocationResult.Granted(fusedLocationClient.lastLocation.await())
        } else {
            LocationResult.NotGranted
        }
    }

    private fun hasLocationPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}