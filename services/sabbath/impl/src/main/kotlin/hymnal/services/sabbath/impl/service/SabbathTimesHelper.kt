// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl.service

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZonedDateTime

interface SabbathTimesHelper {

    /** Friday for the “Sabbath week”. */
    fun fridayOfWeek(): LocalDate

    /** Saturday for the “Sabbath week”. */
    fun saturdayOfWeek(): LocalDate

    /**
     * Compute the effective “week” to use.
     * If it's Saturday night and the sabbathEnd has already passed, roll to next week's Fri/Sat.
     */
    fun weekForQuery(
        now: ZonedDateTime,
        sabbathEndIfKnown: ZonedDateTime?
    ): Pair<LocalDate, LocalDate>

    fun isWithinSabbath(now: ZonedDateTime, start: ZonedDateTime, end: ZonedDateTime): Boolean

    fun hashKey(latitude: Double, longitude: Double): String
}

@ContributesBinding(AppScope::class)
@Inject
class SabbathTimesHelperImpl(
    private val today: () -> LocalDate = { LocalDate.now() }
) : SabbathTimesHelper {

    /** Friday for the “Sabbath week” containing `anchor` (local). */
    override fun fridayOfWeek(): LocalDate = thisFriday()

    /** Saturday for the same week. */
    override fun saturdayOfWeek(): LocalDate = fridayOfWeek().plusDays(1)

    /**
     * Compute the effective “week” to use.
     * If it's Saturday night and the sabbathEnd has already passed, roll to next week's Fri/Sat.
     */
    override fun weekForQuery(
        now: ZonedDateTime,
        sabbathEndIfKnown: ZonedDateTime?
    ): Pair<LocalDate, LocalDate> {
        val fri = fridayOfWeek()
        val sat = saturdayOfWeek()

        // If we already know today’s sabbathEnd and the current time is after it, roll forward.
        if (sabbathEndIfKnown != null && now.isAfter(sabbathEndIfKnown)) {
            val nextFri = fri.plusWeeks(1)
            return nextFri to nextFri.plusDays(1)
        }

        return fri to sat
    }

    override fun isWithinSabbath(
        now: ZonedDateTime,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Boolean = now.isAfter(start) && now.isBefore(end) // [start, end)

    private fun thisFriday(): LocalDate {
        val today = today()
        val daysToAdd = when (today.dayOfWeek) {
            DayOfWeek.SUNDAY -> 5 // Sunday to Friday
            DayOfWeek.MONDAY -> 4 // Monday to Friday
            DayOfWeek.TUESDAY -> 3 // Tuesday to Friday
            DayOfWeek.WEDNESDAY -> 2 // Wednesday to Friday
            DayOfWeek.THURSDAY -> 1 // Thursday to Friday
            DayOfWeek.FRIDAY -> 0  // Friday is today
            DayOfWeek.SATURDAY -> -1 // Saturday to Friday (Don't go forward, stay on this week's Friday)
        }
        return today.plusDays(daysToAdd.toLong())
    }

    override fun hashKey(latitude: Double, longitude: Double): String {
        var latMin = -90.0
        var latMax = 90.0
        var lonMin = -180.0
        var lonMax = 180.0
        var isLon = true
        var bit = 0
        var ch = 0
        val sb = StringBuilder()

        while (sb.length < PRECISION) {
            if (isLon) {
                val mid = (lonMin + lonMax) / 2
                if (longitude > mid) {
                    ch = (ch shl 1) or 1; lonMin = mid
                } else {
                    ch = (ch shl 1); lonMax = mid
                }
            } else {
                val mid = (latMin + latMax) / 2
                if (latitude > mid) {
                    ch = (ch shl 1) or 1; latMin = mid
                } else {
                    ch = (ch shl 1); latMax = mid
                }
            }
            isLon = !isLon
            bit++
            if (bit == 5) {
                sb.append(BASE32[ch])
                bit = 0
                ch = 0
            }
        }
        return sb.toString()
    }

    companion object {
        private const val BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"
        private const val PRECISION = 3  // ~156 km cells → ~50 km “wiggle room”
    }
}