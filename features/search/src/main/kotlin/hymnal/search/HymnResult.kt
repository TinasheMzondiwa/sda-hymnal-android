// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.hymn.NumberText

@Composable
fun HymnResult(hymn: Hymn, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val hapticFeedback = LocalAppHapticFeedback.current

    CenteredCustomListItem(
        headline = hymn.title,
        supportingText = hymn.lyrics
            .firstOrNull()?.lines?.take(3)
            ?.joinToString(separator = "\n") ?: "",
        modifier = modifier
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                hapticFeedback.performClick()
                onClick()
            },
        leadingContent = {
            NumberText(
                number = hymn.number,
                modifier = Modifier,
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
    )
}

@Composable
private fun CenteredCustomListItem(
    headline: String,
    supportingText: String,
    modifier: Modifier = Modifier,
    leadingContent: @Composable () -> Unit = {},
    trailingContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp) // Standard min-height for list items
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Leading Icon
        leadingContent()

        // Text Content
        Column(
            modifier = Modifier.weight(1f), // Take up remaining space
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = headline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp
                ),
            )
            Text(
                text = supportingText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                )
            )
        }

        // Trailing Icon
        trailingContent()
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            HymnResult(hymn = previewHymn, modifier = Modifier.padding(16.dp))
        }
    }
}

internal val previewHymn = Hymn(
    index = "108",
    number = 108,
    title = "Amazing Grace",
    majorKey = null,
    author = "John Newton",
    authorLink = null,
    year = "1985",
    lyrics = listOf(
        HymnLyrics.Verse(
            index = 1,
            lines = listOf(
                "Amazing grace! How sweet the sound",
                "That saved a wretch like me!",
                "I once was lost, but now am found;",
                "Was blind, but now I see.",
            ),
        ),
    ),
    revision = 1,
)