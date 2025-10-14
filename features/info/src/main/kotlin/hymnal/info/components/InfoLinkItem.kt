// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.info.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.info.InfoLink
import hymnal.ui.theme.HymnalTheme

@Composable
fun InfoLinkItem(link: InfoLink, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        ListItem(
            headlineContent = {
                Text(text = stringResource(link.title))
            },
            modifier = Modifier,
            leadingContent = {
                Icon(
                    painter = painterResource(link.icon),
                    contentDescription = null
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null
                )
            }
        )

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 56.dp),
            thickness = 0.5.dp,
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            InfoLinkItem(
                link = InfoLink.Rate,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}