// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.services.sabbath.api.SabbathInfo
import hymnal.services.sabbath.api.SabbathRepository
import hymnal.services.sabbath.impl.service.SabbathTimesHelper
import hymnal.services.sabbath.impl.service.SunriseSunsetService
import hymnal.services.sabbath.impl.service.model.SabbathTimes
import hymnal.storage.db.dao.SabbathTimesDao
import hymnal.storage.db.entity.SabbathTimesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@ContributesBinding(AppScope::class)
@Inject
class SabbathRepositoryImpl(
    private val appContext: Context,
    private val sabbathTimesDao: SabbathTimesDao,
    private val sunriseSunsetService: SunriseSunsetService,
    private val helper: SabbathTimesHelper,
    private val dispatcherProvider: DispatcherProvider,
) : SabbathRepository {

    private val geocoder: Geocoder by lazy { Geocoder(appContext, Locale.getDefault()) }

    override fun getSabbathInfo(
        latitude: Double,
        longitude: Double
    ): Flow<Result<SabbathInfo>> = flow {
        val now = ZonedDateTime.now()

        // Determine which week we should use (handles Sat night rollover)
        val (tFri, _) = helper.weekForQuery(now, sabbathEndIfKnown = null)
        var key = weekDbKey(tFri, latitude, longitude)
        val tCache = sabbathTimesDao.get(key)?.let { createSabbathInfo(it, latitude, longitude) }
        val (fri, sat) = helper.weekForQuery(now, sabbathEndIfKnown = tCache?.sabbathEnd)
        key = weekDbKey(fri, latitude, longitude)

        val cached = withContext(dispatcherProvider.io) {
            sabbathTimesDao.get(key)?.let { createSabbathInfo(it, latitude, longitude) }
        }

        if (cached != null) {
            emit(Result.success(cached))
        }

        // Decide if we need a refresh:
        val needsNetwork = cached == null || shouldRefresh(cached, now)

        if (needsNetwork) {
            emit(
                Result.success(
                    fetchFromNetwork(
                        latitude = latitude,
                        longitude = longitude,
                        fri = fri,
                        sat = sat,
                        now = now,
                    )
                )
            )

        }
    }.flowOn(dispatcherProvider.io)
        .catch { Timber.e(it) }

    private suspend fun fetchFromNetwork(
        latitude: Double,
        longitude: Double,
        fri: LocalDate,
        sat: LocalDate,
        now: ZonedDateTime
    ): SabbathInfo {
        var fridaySunset = withContext(dispatcherProvider.default) {
            sunriseSunsetService.getSunsetTime(
                latitude = latitude,
                longitude = longitude,
                date = weekKeyIso(fri)
            )
        }.getOrThrow()
        var saturdaySunset = withContext(dispatcherProvider.default) {
            sunriseSunsetService.getSunsetTime(
                latitude = latitude,
                longitude = longitude,
                date = weekKeyIso(sat)
            )
        }.getOrThrow()

        if (now.isAfter(saturdaySunset)) {
            val nextFri = fri.plusWeeks(1)
            val nextSat = nextFri.plusDays(1)

            fridaySunset = withContext(dispatcherProvider.default) {
                sunriseSunsetService.getSunsetTime(
                    latitude = latitude,
                    longitude = longitude,
                    date = weekKeyIso(nextFri)
                )
            }.getOrThrow()
            saturdaySunset = withContext(dispatcherProvider.default) {
                sunriseSunsetService.getSunsetTime(
                    latitude = latitude,
                    longitude = longitude,
                    date = weekKeyIso(nextSat)
                )
            }.getOrThrow()
        }
        cacheSabbathTimes(
            id = weekDbKey(fridaySunset.toLocalDate(), latitude, longitude),
            sabbathTimes = SabbathTimes(
                friday = fridaySunset,
                saturday = saturdaySunset
            )
        )

        return SabbathInfo(
            location = getCityAndState(latitude, longitude),
            isSabbath = helper.isWithinSabbath(now, fridaySunset, saturdaySunset),
            sabbathStart = fridaySunset,
            sabbathEnd = saturdaySunset,
        )
    }

    /**
     * Creates a [SabbathInfo] object from [SabbathTimesEntity] or [SabbathTimes].
     */
    private suspend fun createSabbathInfo(
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
            isSabbath = helper.isWithinSabbath(
                now = ZonedDateTime.now(),
                start = friday,
                end = saturday
            ),
            sabbathStart = friday,
            sabbathEnd = saturday,
        )
    }

    private fun String.asDateTime(): ZonedDateTime =
        ZonedDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private fun ZonedDateTime.asDateString(): String =
        this.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private fun weekKeyIso(friday: LocalDate): String = friday.toString() // YYYY-MM-DD

    private fun weekDbKey(friday: LocalDate, latitude: Double, longitude: Double): String =
        "${weekKeyIso(friday)}-${helper.hashKey(latitude, longitude)}"

    /** Refresh policy:
     * - If both start/end are in the past relative to now, refresh.
     * - If record older than 7 days (paranoia / API corrections), refresh.
     */
    private fun shouldRefresh(cached: SabbathInfo, now: ZonedDateTime): Boolean {
        val bothPast = now.isAfter(cached.sabbathEnd)
        val stale = Duration.between(cached.sabbathStart, now).toDays() >= 7
        return bothPast || stale
    }

    @SuppressLint("DeprecatedCall")
    private suspend fun getCityAndState(latitude: Double, longitude: Double): String =
        suspendCancellableCoroutine { continuation ->
            val handleResult: (List<Address>?) -> Unit = { addresses ->
                val result = if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality      // e.g., "Toronto"
                    val state = addresses[0].adminArea    // e.g., "Ontario"

                    when {
                        city != null && state != null -> "$city $state"
                        city != null -> city
                        else -> "Unknown Location"
                    }
                } else {
                    "Unknown Location"
                }

                continuation.resumeWith(Result.success(result))
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1, handleResult)
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                handleResult(addresses)
            }
    }

    private suspend fun cacheSabbathTimes(id: String, sabbathTimes: SabbathTimes) {
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
}