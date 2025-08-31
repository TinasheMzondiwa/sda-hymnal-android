/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */



package hymnal.ui.extensions.modifier

import androidx.compose.ui.Modifier

/**
 * Conditionally applies the [builder] block if [condition].
 */
inline fun Modifier.thenIf(
    condition: Boolean,
    builder: Modifier.() -> Modifier
) = if (condition) builder() else this


