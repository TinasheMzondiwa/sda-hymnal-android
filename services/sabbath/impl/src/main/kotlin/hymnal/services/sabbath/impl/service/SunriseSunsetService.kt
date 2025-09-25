// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl.service

import androidx.annotation.Keep
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
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

    suspend fun getSunsetTime(
        latitude: Double,
        longitude: Double,
        date: String,
    ): Result<ZonedDateTime>
}

@ContributesBinding(AppScope::class)
@Inject
class SunriseSunsetServiceImpl(
    val client: HttpClient,
) : SunriseSunsetService {

    override suspend fun getSunsetTime(
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

@Keep
@Serializable
private data class SunriseSunsetResponse(
    val results: SunriseSunsetResults,
    val status: String
)

@Keep
@Serializable
private data class SunriseSunsetResults(
    @Serializable(ZonedDateTimeSerializer::class)
    val sunset: ZonedDateTime,
)