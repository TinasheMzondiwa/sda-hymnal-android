// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl.service

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.services.sabbath.impl.service.model.SabbathTimes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.time.ZonedDateTime

interface SunriseSunsetService {
    suspend fun getSabbathTimes(
        latitude: Double,
        longitude: Double,
    ): Result<SabbathTimes>
}

@ContributesBinding(AppScope::class)
@Inject
class SunriseSunsetServiceImpl(
    val client: HttpClient,
    val helper: SabbathTimesHelper,
) : SunriseSunsetService {

    override suspend fun getSabbathTimes(
        latitude: Double,
        longitude: Double,
    ): Result<SabbathTimes> {
        return try {
            val friday = getSunsetTime(
                latitude = latitude,
                longitude = longitude,
                date = helper.fridayDate()
            )
            val saturday = getSunsetTime(
                latitude = latitude,
                longitude = longitude,
                date = helper.saturdayDate()
            )
            if (friday.isSuccess && saturday.isSuccess) {
                Result.success(
                    SabbathTimes(
                        friday = friday.getOrThrow(),
                        saturday = saturday.getOrThrow()
                    )
                )
            } else {
                Result.failure(Exception("Error fetching sunset times"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getSunsetTime(
        latitude: Double,
        longitude: Double,
        date: String,
    ): Result<ZonedDateTime> {
        val response: HttpResponse = client.get {
            url(SUNRISE_SUNSET_API_URL)
            parameter("lat", latitude)
            parameter("lng", longitude)
            parameter("date", date)
            parameter("formatted", 0) // ISO 8601 format
            parameter("tzid", ZoneId.systemDefault().id)
        }
        return if (response.status.value in 200..299) {
            val body: SunriseSunsetResponse = response.body()
            Result.success(body.results.sunset)
        } else {
            Result.failure(Exception("Error fetching sunrise/sunset data: ${response.status}"))
        }
    }


    companion object {
        private const val SUNRISE_SUNSET_API_URL = "https://api.sunrise-sunset.org/json"
    }
}

@Serializable
private data class SunriseSunsetResponse(
    val results: SunriseSunsetResults,
    val status: String
)

@Serializable
private data class SunriseSunsetResults(
    @Serializable(ZonedDateTimeSerializer::class)
    val sunset: ZonedDateTime,
)