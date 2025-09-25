// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl.service

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class SabbathTimesHelperTest {

    private val thursdaySep25 = LocalDate.of(2025, 9, 25)

    private val helper: SabbathTimesHelper = SabbathTimesHelperImpl(
        today = { thursdaySep25 }
    )

    @Test
    fun `fridayOfWeek should return correct date`() {
        val expected = LocalDate.of(2025, 9, 26)
        assertThat(helper.fridayOfWeek()).isEqualTo(expected)
    }

    @Test
    fun `fridayOfWeek should return correct date when today is Friday`() {
        val today = LocalDate.of(2025, 9, 26) // Friday
        val helper = SabbathTimesHelperImpl(today = { today })

        assertThat(helper.fridayOfWeek()).isEqualTo(today)
    }

    @Test
    fun `fridayOfWeek should return correct date when today is Saturday`() {
        val helper = SabbathTimesHelperImpl(
            today = { LocalDate.of(2025, 9, 27) } // Saturday
        )
        val expected = LocalDate.of(2025, 9, 26)
        assertThat(helper.fridayOfWeek()).isEqualTo(expected)
    }

    @Test
    fun `saturdayOfWeek should return correct date`() {
        val expected = LocalDate.of(2025, 9, 27)
        assertThat(helper.saturdayOfWeek()).isEqualTo(expected)
    }

    @Test
    fun `saturdayOfWeek should return correct date when today is Saturday`() {
        val today = LocalDate.of(2025, 9, 27) // Saturday
        val helper = SabbathTimesHelperImpl(today = { today })

        assertThat(helper.saturdayOfWeek()).isEqualTo(today)
    }

    @Test
    fun `weekForQuery should return correct dates`() {
        val expected = Pair(
            LocalDate.of(2025, 9, 26),
            LocalDate.of(2025, 9, 27)
        )

        val result = helper.weekForQuery(
            now = thursdaySep25.atStartOfDay().atZone(ZoneId.systemDefault()),
            sabbathEndIfKnown = null
        )

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `weekForQuery should roll over to next week if after sabbathEnd`() {
        val expected = Pair(
            LocalDate.of(2025, 10, 3),
            LocalDate.of(2025, 10, 4)
        )

        val result = helper.weekForQuery(
            now = LocalDate.of(2025, 9, 28).atStartOfDay().atZone(ZoneId.systemDefault()), // Sunday
            sabbathEndIfKnown = LocalDate.of(2025, 9, 27).atTime(19, 0)
                .atZone(ZoneId.systemDefault()) // Sep 27, 7 PM
        )

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `weekForQuery should roll over to next week if after sabbath passed`() {
        val expected = Pair(
            LocalDate.of(2025, 10, 3),
            LocalDate.of(2025, 10, 4)
        )

        val result = helper.weekForQuery(
            now = LocalDate.of(2025, 9, 28).atTime(19, 30)
                .atZone(ZoneId.systemDefault()), // Sep 28, 7:30 PM
            sabbathEndIfKnown = LocalDate.of(2025, 9, 28).atTime(19, 0)
                .atZone(ZoneId.systemDefault()) // Sep 28, 7 PM
        )

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `isWithinSabbath should return true for time within sabbath`() {
        val start =
            LocalDate.of(2025, 9, 26).atTime(18, 0).atZone(ZoneId.systemDefault()) // Sep 26, 6 PM
        val end =
            LocalDate.of(2025, 9, 27).atTime(19, 0).atZone(ZoneId.systemDefault()) // Sep 27, 7 PM
        val now =
            LocalDate.of(2025, 9, 27).atTime(12, 0).atZone(ZoneId.systemDefault()) // Sep 27, Noon

        assertThat(helper.isWithinSabbath(now, start, end)).isTrue()
    }

    @Test
    fun `isWithinSabbath should return false for time before sabbath`() {
        val start =
            LocalDate.of(2025, 9, 26).atTime(18, 0).atZone(ZoneId.systemDefault()) // Sep 26, 6 PM
        val end =
            LocalDate.of(2025, 9, 27).atTime(19, 0).atZone(ZoneId.systemDefault()) // Sep 27, 7 PM
        val now =
            LocalDate.of(2025, 9, 26).atTime(17, 0).atZone(ZoneId.systemDefault()) // Sep 26, 5 PM

        assertThat(helper.isWithinSabbath(now, start, end)).isFalse()
    }

    @Test
    fun `isWithinSabbath should return false for time after sabbath`() {
        val start =
            LocalDate.of(2025, 9, 26).atTime(18, 0).atZone(ZoneId.systemDefault()) // Sep 26, 6 PM
        val end =
            LocalDate.of(2025, 9, 27).atTime(19, 0).atZone(ZoneId.systemDefault()) // Sep 27, 7 PM
        val now =
            LocalDate.of(2025, 9, 27).atTime(20, 0).atZone(ZoneId.systemDefault()) // Sep 27, 8 PM

        assertThat(helper.isWithinSabbath(now, start, end)).isFalse()
    }

    @Test
    fun `hashKey should return consistent hash for same coordinates`() {
        val lat = 40.7128
        val lon = -74.0060 // New York City

        val hash1 = helper.hashKey(lat, lon)
        val hash2 = helper.hashKey(lat, lon)

        assertThat(hash1).isEqualTo(hash2)
    }

    @Test
    fun `hashKey should return different hashes for different coordinates`() {
        val lat1 = 40.7128
        val lon1 = -74.0060 // New York City
        val lat2 = 34.0522
        val lon2 = -118.2437 // Los Angeles

        val hash1 = helper.hashKey(lat1, lon1)
        val hash2 = helper.hashKey(lat2, lon2)

        assertThat(hash1).isNotEqualTo(hash2)
    }

}