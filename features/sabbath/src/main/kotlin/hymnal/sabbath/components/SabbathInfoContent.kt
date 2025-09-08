// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.sabbath.State
import hymnal.ui.theme.HymnalTheme

internal fun LazyListScope.sabbathInfo(state: State.SabbathInfo, colors: SabbathColors) {
    item("location") {
        Row(
            modifier = Modifier
                .animateItem()
                .padding(top = 16.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    color = colors.trackNeutral,
                    shape = RoundedCornerShape((24.dp))
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
                text = state.location,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colors.text
            )
        }
    }

    item("sabbath_card") {
        SabbathCard(
            bigCountdown = state.countDown,
            sabbathStart = state.sabbathStart,
            sabbathEnd = state.sabbathEnd,
            isSabbath = state.isSabbath,
            progress = state.progress,
            colors = colors,
            modifier = Modifier
                .animateItem()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewContent(
        state = State.SabbathInfo(
            location = "Harare, Zimbabwe",
            isSabbath = false,
            progress = 0f,
            countDown = "1d 3h 05m",
            sabbathStart = "Fri, 6:00 PM",
            sabbathEnd = "Sat, 7:00 PM",
        ),
        modifier = Modifier.padding(16.dp),
    )
}

@PreviewLightDark
@Composable
private fun PreviewSabbath() {
    PreviewContent(
        state = State.SabbathInfo(
            location = "Toronto, Ontario",
            isSabbath = true,
            progress = 0.8f,
            countDown = "3h 05m",
            sabbathStart = "Fri, 6:30 PM",
            sabbathEnd = "Sat, 7:00 PM",
        ),
        modifier = Modifier.padding(16.dp),
    )
}

@Composable
private fun PreviewContent(state: State.SabbathInfo, modifier: Modifier = Modifier) {
    val colors = rememberSabbathColors(isDark = isSystemInDarkTheme())
    HymnalTheme {
        Surface {
            LazyColumn(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                sabbathInfo(state = state, colors = colors)
            }
        }
    }
}