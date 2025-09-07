// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.model

import androidx.compose.runtime.Stable

@Stable
sealed interface HymnLyrics {
    val index: Int
    val lines: List<String>

    data class Verse(
        override val index: Int,
        override val lines: List<String>,
    ) : HymnLyrics

    data class Chorus(
        override val index: Int,
        override val lines: List<String>,
    ) : HymnLyrics
}