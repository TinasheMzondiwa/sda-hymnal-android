// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components.info

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.sabbath.components.SabbathCard
import hymnal.sabbath.components.SabbathColors
import hymnal.sabbath.components.SabbathInfoItem
import hymnal.sabbath.components.rememberSabbathColors
import hymnal.ui.theme.HymnalTheme

@Immutable
data class SabbathInfoCard(
    val isSabbath: Boolean,
    val progress: Float,
    val countDown: String,
    val sabbathStart: String,
    val sabbathEnd: String,
) : SabbathInfoItem {

    override val id: String = "sabbath_card"

    @Composable
    override fun Content(colors: SabbathColors, modifier: Modifier) {
        SabbathCard(
            bigCountdown = countDown,
            sabbathStart = sabbathStart,
            sabbathEnd = sabbathEnd,
            isSabbath = isSabbath,
            progress = progress,
            colors = colors,
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    val colors = rememberSabbathColors(isDark = isSystemInDarkTheme())

    HymnalTheme {
        Surface {
            SabbathInfoCard(
                isSabbath = true,
                progress = 0.75f,
                countDown = "12:34:56",
                sabbathStart = "Fri, 6:00 PM",
                sabbathEnd = "Sat, 7:00 PM",
            ).Content(colors)
        }
    }
}
