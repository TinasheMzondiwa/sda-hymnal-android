// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.fakes

import hymnal.libraries.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class TestDispatcherProvider(private val testDispatcher: TestDispatcher) : DispatcherProvider {

    @OptIn(ExperimentalCoroutinesApi::class)
    constructor() : this(testDispatcher = UnconfinedTestDispatcher())

    override val io: CoroutineDispatcher = testDispatcher
    override val main: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}
