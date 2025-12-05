// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components.filters

import androidx.compose.runtime.Stable

@Stable
sealed interface FilterItem {
    val selected: Boolean

    data class Hymnal(
        val title: String,
        override val selected: Boolean
    ) : FilterItem

    data class Sort(
        val titleRes: Int,
        val leadingIcon: Int,
        override val selected: Boolean
    ) : FilterItem
}