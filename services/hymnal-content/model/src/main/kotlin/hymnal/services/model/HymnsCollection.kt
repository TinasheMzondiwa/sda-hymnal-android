// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.model

import androidx.compose.runtime.Immutable

@Immutable
data class HymnsCollection(
    val collectionId: String,
    val title: String,
    val description: String?,
    val hymns: List<Hymn>,
    val created: Long,
    val color: String,
)