// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.ui.theme.size

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hymnal.ui.extensions.LocalWindowWidthSizeClass

object HymnalDimens {

    @Composable
    fun horizontalPadding(compatPadding: Dp = 16.dp): Dp {
        val windowWidthSizeClass = LocalWindowWidthSizeClass.current

        return if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
            compatPadding
        } else {
            64.dp
        }
    }
}