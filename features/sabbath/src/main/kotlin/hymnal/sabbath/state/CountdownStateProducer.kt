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
                emit("0d 0h 00m")
                break
            }

            // Calculate days, hours, and minutes from the remaining duration.
            val days = duration.toDays()
            val hours = duration.toHours() % 24
            val minutes = duration.toMinutes() % 60

            // Format the output string with a two-digit minutes value.
            val formattedString = if (isSabbath) {
                String.format(Locale.getDefault(), "%dh %02dm", hours, minutes)
            } else {
                String.format(Locale.getDefault(), "%dd %dh %02dm", days, hours, minutes)
            }
            emit(formattedString)
            println("Emitted countdown: $formattedString")

            // Wait for one minute before the next calculation and emission.
            delay(60 * 1000L)
        }
    }.flowOn(dispatcherProvider.default)
}