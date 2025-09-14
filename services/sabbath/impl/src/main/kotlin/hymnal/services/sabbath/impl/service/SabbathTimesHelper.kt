// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl.service

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

interface SabbathTimesHelper {

    /** Friday for the “Sabbath week” containing `anchor` (local). */
    fun fridayOfWeek(anchor: ZonedDateTime): LocalDate

    /** Saturday for the same week. */
    fun saturdayOfWeek(anchor: ZonedDateTime): LocalDate

    /**
     * Compute the effective “week” to use.
     * If it's Saturday night and the sabbathEnd has already passed, roll to next week's Fri/Sat.
     */
    fun weekForQuery(
        now: ZonedDateTime,
        sabbathEndIfKnown: ZonedDateTime?
    ): Pair<LocalDate, LocalDate>

    fun isWithinSabbath(now: ZonedDateTime, start: ZonedDateTime, end: ZonedDateTime): Boolean
}

@ContributesBinding(AppScope::class)
@Inject
class SabbathTimesHelperImpl(
    private val today: () -> LocalDate = { LocalDate.now() }
) : SabbathTimesHelper {

    /** Friday for the “Sabbath week” containing `anchor` (local). */
    override fun fridayOfWeek(anchor: ZonedDateTime): LocalDate =
        anchor.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY))

    /** Saturday for the same week. */
    override fun saturdayOfWeek(anchor: ZonedDateTime): LocalDate =
        anchor.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY))

    /**
     * Compute the effective “week” to use.
     * If it's Saturday night and the sabbathEnd has already passed, roll to next week's Fri/Sat.
     */
    override fun weekForQuery(
        now: ZonedDateTime,
        sabbathEndIfKnown: ZonedDateTime?
    ): Pair<LocalDate, LocalDate> {
        val fri = fridayOfWeek(now)
        val sat = saturdayOfWeek(now)

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
    ): Boolean =
        !now.isBefore(start) && now.isBefore(end) // [start, end)

    private fun thisFriday(end: ZonedDateTime?): LocalDate {
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
        val friday = today.plusDays(daysToAdd.toLong())

        return if (end != null && ZonedDateTime.now(end.zone).isAfter(end)) {
            // If the current time is past the end time, return next week's Friday
            friday.plusWeeks(1)
        } else {
            friday
        }
    }
}