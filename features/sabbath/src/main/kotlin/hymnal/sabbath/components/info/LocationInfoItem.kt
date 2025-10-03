// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components.info

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.sabbath.components.SabbathColors
import hymnal.sabbath.components.SabbathInfoItem
import hymnal.sabbath.components.rememberSabbathColors
import hymnal.ui.theme.HymnalTheme

@Immutable
data class LocationInfoItem(val location: String) : SabbathInfoItem {
    override val id: String = "location"

    @Composable
    override fun Content(colors: SabbathColors, modifier: Modifier) {
        Row(
            modifier = modifier
                .padding(top = 16.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    color = colors.trackNeutral, shape = RoundedCornerShape((24.dp))
                )
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = colors.text
            )

            Text(
                text = location, maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colors.text,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    val colors = rememberSabbathColors(isDark = isSystemInDarkTheme())

    HymnalTheme {
        Surface {
            LocationInfoItem(location = "Cape Town, Western Cape").Content(
                colors,
                Modifier.padding(16.dp)
            )
        }
    }
}
