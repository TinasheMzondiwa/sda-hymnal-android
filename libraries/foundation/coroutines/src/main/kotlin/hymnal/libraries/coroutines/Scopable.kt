// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

interface Scopable {
    val scope: CoroutineScope
}

fun ioScopable(dispatcherProvider: DispatcherProvider): Scopable = object : Scopable {
    override val scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)
}
