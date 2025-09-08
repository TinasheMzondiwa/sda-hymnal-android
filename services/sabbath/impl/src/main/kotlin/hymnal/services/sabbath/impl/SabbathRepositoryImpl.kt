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
import hymnal.services.sabbath.impl.service.model.SabbathTimes
import hymnal.services.sabbath.impl.service.SabbathTimesHelper
import hymnal.services.sabbath.impl.service.SunriseSunsetService
import hymnal.storage.db.dao.SabbathTimesDao
import hymnal.storage.db.entity.SabbathTimesEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@ContributesBinding(AppScope::class)
@Inject
class SabbathRepositoryImpl(
    private val appContext: Context,
    private val helper: SabbathTimesHelper,
    private val sabbathTimesDao: SabbathTimesDao,
    private val sunriseSunsetService: SunriseSunsetService,
    private val dispatcherProvider: DispatcherProvider,
) : SabbathRepository {

    private val sabbathIdFlow = MutableStateFlow(helper.sabbathDayId())
    private val geocoder: Geocoder by lazy { Geocoder(appContext, Locale.getDefault())  }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSabbathInfo(
        latitude: Double,
        longitude: Double
    ): Flow<Result<SabbathInfo>> {
        return sabbathIdFlow
            .flatMapLatest { sabbathTimesDao.getSabbathTimes(it) }
            .map { cachedSabbathTimes ->
                if (cachedSabbathTimes != null) {
                    Result.success(createSabbathInfo(cachedSabbathTimes, latitude, longitude))
                } else {
                    // If not in cache, fetch from network
                    fetchAndCacheSabbathInfo(latitude, longitude)
                }
            }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(Result.failure(it))
            }
    }

    suspend fun fetchAndCacheSabbathInfo(
        latitude: Double,
        longitude: Double
    ): Result<SabbathInfo> {
        return withContext(dispatcherProvider.default) {
            val response = sunriseSunsetService.getSabbathTimes(
                latitude = latitude,
                longitude = longitude,
            )

            response.map { sabbathTimes ->
                cacheSabbathTimes(sabbathTimes)
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
            is SabbathTimesEntity -> sabbathTimes.friday.asDateTime() to sabbathTimes.saturday.asDateTime()
            is SabbathTimes -> sabbathTimes.friday to sabbathTimes.saturday
            else -> throw IllegalArgumentException("Unsupported type for sabbathTimes: ${sabbathTimes::class.java.name}")
        }
        return SabbathInfo(
            location = getCityAndState(latitude, longitude),
            isSabbath = isSabbathDay(start = friday, end = saturday),
            sabbathStart = friday,
            sabbathEnd = saturday,
        ).also { sabbathIdFlow.update { helper.sabbathDayId(end = saturday) } }
    }

    private suspend fun cacheSabbathTimes(sabbathTimes: SabbathTimes) {
        val id = sabbathIdFlow.value
        withContext(dispatcherProvider.io) {
            sabbathTimesDao.insert(
                SabbathTimesEntity(
                    id = id,
                    friday = sabbathTimes.friday.asDateString(),
                    saturday = sabbathTimes.saturday.asDateString()
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
    private fun isSabbathDay(start: ZonedDateTime, end: ZonedDateTime): Boolean {
        val now = ZonedDateTime.now(start.zone) // use same timezone as input
        return !now.isBefore(start) && !now.isAfter(end)
    }

    private fun getCityAndState(latitude: Double, longitude: Double): String {
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

    private fun String.asDateTime(): ZonedDateTime =
        ZonedDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private fun ZonedDateTime.asDateString(): String =
        this.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}