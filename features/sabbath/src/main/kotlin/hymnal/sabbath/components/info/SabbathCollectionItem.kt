// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hymnal.sabbath.Event
import hymnal.sabbath.components.SabbathColors
import hymnal.sabbath.components.SabbathInfoItem
import hymnal.libraries.l10n.R as L10nR
import hymnal.ui.R as UiR

data class SabbathCollectionItem(val eventSink: (Event.SabbathInfo) -> Unit) : SabbathInfoItem {
    override val id: String = "collection"

    @Composable
    override fun Content(
        colors: SabbathColors,
        modifier: Modifier
    ) {
        ListItem(
            headlineContent = {
                Text(text = stringResource(L10nR.string.sabbath_playlist))
            },
            modifier = modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = { eventSink(Event.SabbathInfo.OnSabbathHymnsClicked) }),
            supportingContent = {
                Text(text = stringResource(L10nR.string.sabbath_playlist_sub_title))
            },
            leadingContent = {
                Icon(
                    painter = painterResource(UiR.drawable.ic_queue_music),
                    contentDescription = null,
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                headlineColor = colors.text,
                supportingColor = colors.textSecondary,
                leadingIconColor = colors.textSecondary
            )
        )
    }
}