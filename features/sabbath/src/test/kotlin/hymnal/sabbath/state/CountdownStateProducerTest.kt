// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.state

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import hymnal.libraries.coroutines.test.CoroutinesRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime

class CountdownStateProducerTest {

    @get:Rule val coroutineRule = CoroutinesRule()

    private val producer = CountdownStateProducerImpl(coroutineRule.dispatcherProvider)

    @Test
    fun `emits hours and minutes when isSabbath and hours greater than zero`() = runTest {
        val now = ZonedDateTime.now()
        val target = now.plusHours(2).plusMinutes(5)

        producer.invoke(targetDateTime = target, isSabbath = true).test {
            val first = awaitItem()
            assertThat(first).isEqualTo("2h 4m")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits minutes only when isSabbath and hours are zero`() = runTest {
        val now = ZonedDateTime.now()
        val target = now.plusMinutes(6)

        producer.invoke(targetDateTime = target, isSabbath = true).test {
            val first = awaitItem()
            assertThat(first).isEqualTo("5 minutes")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits days hours minutes when not Sabbath and days greater than zero`() = runTest {
        val now = ZonedDateTime.now()
        val target = now.plusDays(3).plusHours(3).plusMinutes(4)

        producer.invoke(targetDateTime = target, isSabbath = false).test {
            val first = awaitItem()
            assertThat(first).isEqualTo("3d 3h")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `always shows minutes when hours greater than zero (not Sabbath)`() = runTest {
        val now = ZonedDateTime.now()
        val target = now.plusHours(5) // minutes = 0

        producer.invoke(targetDateTime = target, isSabbath = false).test {
            val first = awaitItem()
            assertThat(first).isEqualTo("4h 59m")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits dashes and completes when target time reached or past`() = runTest {
        val now = ZonedDateTime.now()
        val target = now.minusMinutes(1)

        producer.invoke(targetDateTime = target, isSabbath = false).test {
            assertThat(awaitItem()).isEqualTo("-- -- --")
            awaitComplete()
        }
    }
}