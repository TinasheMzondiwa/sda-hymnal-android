// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.model

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable

@Keep
@Immutable
data class Hymn(
    val index: String,
    val number: Int,
    val title: String,
    val majorKey: String?,
    val lyrics: List<HymnLyrics>,
)
