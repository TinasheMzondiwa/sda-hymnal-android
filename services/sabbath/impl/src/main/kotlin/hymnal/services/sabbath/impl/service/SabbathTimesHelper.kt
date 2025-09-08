// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl.service

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

interface SabbathTimesHelper {

    /**
     * Get the date of the current week's Friday in ISO format (yyyy-MM-dd).
     * Week is considered Sunday -> Saturday.
     *
     * @return A string representing the current week's Friday's date.
     */
    fun fridayDate(): String

    /**
     * Get the date of the current week's Saturday in ISO format (yyyy-MM-dd).
     * Week is considered Sunday -> Saturday.
     *
     * @return A string representing the current week's Saturday's date.
     */
    fun saturdayDate(): String

    /**
     * Get the date of the current week's Friday in ISO format (yyyy-MM-dd).
     * If the current time is now past the [end] then return the next week's Friday.
     *
     * Week is considered Sunday → Saturday.
     */
    fun sabbathDayId(end: String? = null): String
}

@ContributesBinding(AppScope::class)
@Inject
class SabbathTimesHelperImpl(
    private val today: () -> LocalDate = { LocalDate.now() }
) : SabbathTimesHelper {

    override fun fridayDate(): String {
        return thisFriday().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    override fun saturdayDate(): String {
        val friday = thisFriday()
        return friday.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    override fun sabbathDayId(end: String?): String {
        val thisFriday = thisFriday()

        // Check if the current time is past the provided end time.
        val shouldUseNextFriday = end?.let {
            val endTime = ZonedDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            ZonedDateTime.now(endTime.zone).isAfter(endTime)
        } ?: false

        // If the end time has passed, get next week's Friday, otherwise use the current week's.
        val targetFriday = if (shouldUseNextFriday) {
            thisFriday.plusWeeks(1)
        } else {
            thisFriday
        }

        return targetFriday.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    private fun thisFriday(): LocalDate {
        val today = today()
        val daysToAdd = when (today.dayOfWeek) {
            DayOfWeek.SUNDAY -> 5 // Sunday to Friday
            DayOfWeek.MONDAY -> 4 // Monday to Friday
            DayOfWeek.TUESDAY -> 3 // Tuesday to Friday
            DayOfWeek.WEDNESDAY -> 2 // Wednesday to Friday
            DayOfWeek.THURSDAY -> 1 // Thursday to Friday
            DayOfWeek.FRIDAY -> 0  // Friday is today
            DayOfWeek.SATURDAY -> 0 // Saturday to Friday (Don't go forward, stay on this week's Friday)
        }
        return today.plusDays(daysToAdd.toLong())
    }
}