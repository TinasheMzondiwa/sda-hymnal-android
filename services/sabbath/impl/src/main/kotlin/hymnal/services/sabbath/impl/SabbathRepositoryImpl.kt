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
import hymnal.services.sabbath.impl.service.SabbathTimes
import hymnal.services.sabbath.impl.service.SunriseSunsetService
import hymnal.services.sabbath.impl.service.fridayDate
import hymnal.storage.db.dao.SabbathTimesDao
import hymnal.storage.db.entity.SabbathTimesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@ContributesBinding(AppScope::class)
@Inject
class SabbathRepositoryImpl(
    private val appContext: Context,
    private val sabbathTimesDao: SabbathTimesDao,
    private val sunriseSunsetService: SunriseSunsetService,
    private val dispatcherProvider: DispatcherProvider,
) : SabbathRepository {

    private val geocoder: Geocoder by lazy { Geocoder(appContext, Locale.getDefault())  }

    override fun getSabbathInfo(
        latitude: Double,
        longitude: Double
    ): Flow<Result<SabbathInfo>> {
        val id = fridayDate()

        return sabbathTimesDao.getSabbathTimes(id)
            .map { cachedSabbathTimes ->
                if (cachedSabbathTimes != null) {
                    Result.success(createSabbathInfo(cachedSabbathTimes, latitude, longitude))
                } else {
                    // If not in cache, fetch from network
                    fetchAndCacheSabbathInfo(id, latitude, longitude)
                }
            }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(Result.failure(it))
            }
    }

    suspend fun fetchAndCacheSabbathInfo(
        id: String,
        latitude: Double,
        longitude: Double
    ): Result<SabbathInfo> {
        return withContext(dispatcherProvider.default) {
            val response = sunriseSunsetService.getSabbathTimes(
                latitude = latitude,
                longitude = longitude,
            )

            response.map { sabbathTimes ->
                cacheSabbathTimes(id = id, sabbathTimes)
                createSabbathInfo(sabbathTimes, latitude, longitude)
            }
        }
    }

    /**
     * Creates a [SabbathInfo] object from [SabbathTimesEntity] or [SabbathTimes].
     */
    private fun createSabbathInfo(
        sabbathTimes: Any,
        latitude: Double,
        longitude: Double
    ): SabbathInfo {
        val (friday, saturday) = when (sabbathTimes) {
            is SabbathTimesEntity -> sabbathTimes.friday to sabbathTimes.saturday
            is SabbathTimes -> sabbathTimes.friday to sabbathTimes.saturday
            else -> throw IllegalArgumentException("Unsupported type for sabbathTimes: ${sabbathTimes::class.java.name}")
        }
        return SabbathInfo(
            location = getCityAndState(latitude, longitude),
            isSabbath = isSabbathDay(start = friday, end = saturday),
            sabbathStart = friday,
            sabbathEnd = saturday,
        )
    }

    private suspend fun cacheSabbathTimes(id: String, sabbathTimes: SabbathTimes) {
        withContext(dispatcherProvider.io) {
            sabbathTimesDao.insert(
                SabbathTimesEntity(
                    id = id,
                    friday = sabbathTimes.friday,
                    saturday = sabbathTimes.saturday
                )
            )
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