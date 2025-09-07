// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl

import android.content.Context
import android.location.Geocoder
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.services.sabbath.api.SabbathInfo
import hymnal.services.sabbath.api.SabbathRepository
import hymnal.services.sabbath.impl.service.SunriseSunsetService
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@ContributesBinding(AppScope::class)
@Inject
class SabbathRepositoryImpl(
    private val appContext: Context,
    private val sunriseSunsetService: SunriseSunsetService,
    private val dispatcherProvider: DispatcherProvider,
) : SabbathRepository {

    private val geocoder: Geocoder by lazy { Geocoder(appContext, Locale.getDefault())  }

    override suspend fun getSabbathInfo(
        latitude: Double,
        longitude: Double
    ): Result<SabbathInfo> {
        return withContext(dispatcherProvider.default) {
            val response = sunriseSunsetService.getSunriseSunset(
                latitude = latitude,
                longitude = longitude,
            )

            response.map { sabbathTimes ->
                SabbathInfo(
                    location = getCityAndState(latitude, longitude),
                    isSabbath = isSabbathDay(sabbathTimes.friday, sabbathTimes.saturday),
                    sabbathStart = sabbathTimes.friday,
                    sabbathEnd = sabbathTimes.saturday,
                )
            }
        }
    }

    /**
     * Check if today is the Sabbath.
     *
     * @param start The start time of Sabbath in the format (2025-09-07T23:44:58+00:00).
     * @param end The end time of Sabbath in the format (2025-09-07T10:45:34+00:00).
     */
    private fun isSabbathDay(start: String, end: String): Boolean {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val sabbathStart = ZonedDateTime.parse(start, formatter)
        val sabbathEnd = ZonedDateTime.parse(end, formatter)
        val now = ZonedDateTime.now(sabbathStart.zone) // use same timezone as input

        return !now.isBefore(sabbathStart) && !now.isAfter(sabbathEnd)
    }

    fun getCityAndState(latitude: Double, longitude: Double): String {
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(
            latitude, longitude, 1)

        return if (!addresses.isNullOrEmpty()) {
            val city = addresses[0].locality      // e.g., "Toronto"
            val state = addresses[0].adminArea    // e.g., "Ontario"

            if (city != null && state != null) {
                "$city $state"
            } else {
                city
            }
        } else "Unknown Location"
    }
}