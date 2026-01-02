// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.playback

import androidx.compose.runtime.Immutable

@Immutable
data class TuneItem(
    val index: String,
    val number: Int,
    val title: String,
    val hymnal: String,
)
