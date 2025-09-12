// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.state

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Duration
import java.time.ZonedDateTime
import java.util.Locale

interface CountdownStateProducer {
    operator fun invoke(targetDateTime: ZonedDateTime, isSabbath: Boolean): Flow<String>
}

@ContributesBinding(AppScope::class)
@Inject
class CountdownStateProducerImpl(private val dispatcherProvider: DispatcherProvider) : CountdownStateProducer {
    override fun invoke(targetDateTime: ZonedDateTime, isSabbath: Boolean): Flow<String> = flow {
        while (true) {
            // Calculate the duration between the current time and the target time.
            val now = ZonedDateTime.now()
            val duration = Duration.between(now, targetDateTime)

            // If the duration is negative or zero, the target time has been reached.
            if (duration.isNegative || duration.isZero) {
                emit("-- -- --")
                break
            }

            // Calculate days, hours, and minutes from the remaining duration.
            val days = duration.toDays()
            val hours = duration.toHours() % 24
            val minutes = duration.toMinutes() % 60

            val formattedString = formattedString(days, hours, minutes, isSabbath)
            emit(formattedString)

            // Wait for one minute before the next calculation and emission.
            delay(60 * 1000L)
        }
    }.flowOn(dispatcherProvider.default)

    private fun formattedString(days: Long, hours: Long, minutes: Long, isSabbath: Boolean): String {
        return buildString {
            if (isSabbath) {
                if (hours > 0) {
                    append(String.format(Locale.getDefault(), "%dh ", hours))
                    append("${minutes}m")
                } else if (minutes > 0) {
                    val format = if (minutes == 1L) "%d minute" else "%d minutes"
                    append(String.format(Locale.getDefault(), format, minutes))
                }
            } else {
                if (days > 0) {
                    append(String.format(Locale.getDefault(), "%dd %dh %02dm", days, hours, minutes))
                } else {
                    if (hours > 0) {
                        append(String.format(Locale.getDefault(), "%dh ", hours))
                        append("${minutes}m")
                    } else if (minutes > 0) {
                        val format = if (minutes == 1L) "%d minute" else "%d minutes"
                        append(String.format(Locale.getDefault(), format, minutes))
                    }
                }
            }

            if (isEmpty()) append("0m") // fallback when everything is 0
        }.trim()
    }
}