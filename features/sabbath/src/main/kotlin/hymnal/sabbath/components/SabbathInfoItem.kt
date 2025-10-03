// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
interface SabbathInfoItem {
    val id: String

    @Composable
    fun Content(colors: SabbathColors, modifier: Modifier = Modifier)
}