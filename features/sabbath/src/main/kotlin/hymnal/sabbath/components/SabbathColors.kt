// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Immutable
internal data class SabbathColors(
    val bg: Color,
    val card: Color,
    val text: Color,
    val textSecondary: Color,
    val accent: Color,
    val accentDeep: Color,
    val trackNeutral: Color,
    val gradientTop: Color,
    val gradientBottom: Color,
    val outline: Color,
    val thumbSurface: Color,
)

@Composable
internal fun rememberSabbathColors(isDark: Boolean): SabbathColors = remember(isDark) {
    if (isDark) {
        SabbathColors(
            bg = Color(0xFF0D0D0D),
            card = Color(0xFF131313),
            text = Color(0xFFEDEDED),
            textSecondary = Color(0xFFCDCDCD),
            accent = Color(0xFFF9C44D),
            accentDeep = Color(0xFFEEA52E),
            trackNeutral = Color(0xFF2A2A2A),
            gradientTop = Color(0x14F9C44D),   // ~8% alpha
            gradientBottom = Color(0x14B35C1E),
            outline = Color(0x40000000),        // ~25% black
            thumbSurface = Color(0xFF2A2A2A)
        )
    } else {
        SabbathColors(
            bg = Color(0xFFFFF8F0),
            card = Color(0xFFFFFFFF),
            text = Color(0xFF1E1E1E),
            textSecondary = Color(0xFF4D4D4D),
            accent = Color(0xFFDF8E00),
            accentDeep = Color(0xFFB46F00),
            trackNeutral = Color(0xFFEAE7E2),
            gradientTop = Color(0x1AFFE3B0),   // ~10% alpha
            gradientBottom = Color(0x1AFFD1B3),
            outline = Color(0x1A000000),        // ~10% black for light mode
            thumbSurface = Color(0xFFFFFFFF)
        )
    }
}