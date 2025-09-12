// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.coroutines.test

import hymnal.libraries.coroutines.DispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesRule internal constructor(
    internal val sharedTestDispatcher: TestDispatcher = defaultTestDispatcher(),
    val dispatcherProvider: DispatcherProvider = TestDispatcherProvider(sharedTestDispatcher)
) : TestWatcher() {

    override fun starting(description: Description?) {
        Dispatchers.setMain(dispatcherProvider.main)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }

    companion object {
        @JvmName("create")
        operator fun invoke(): CoroutinesRule = CoroutinesRule(defaultTestDispatcher())

        private fun defaultTestDispatcher(scheduler: TestCoroutineScheduler? = null) =
            UnconfinedTestDispatcher(scheduler)
    }
}