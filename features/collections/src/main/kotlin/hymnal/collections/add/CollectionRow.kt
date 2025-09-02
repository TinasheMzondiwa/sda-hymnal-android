// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.collections.components.CollectionColor
import hymnal.services.model.HymnsCollection
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.color.toColor

@Composable
internal fun CollectionRow(
    spec: ChooseCollectionSpec,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val collection = spec.collection
    ListItem(
        headlineContent = {
            Text(
                text = collection.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (spec.isSelected) FontWeight.Bold else FontWeight.Normal
                )
            )
        },
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        supportingContent = {
            collection.description?.takeUnless { it.isEmpty() }?.let {
                Text(
                    text = it,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(collection.color.toColor(), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (spec.isSelected) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.Black,
                    )
                }
            }
        },
    )
}

internal val sampleCollection = HymnsCollection(
    collectionId = "1",
    title = "Favorites",
    description = "My favorite hymns",
    hymns = emptyList(),
    created = 1686000000000,
    color = CollectionColor.coralOrange.hex,
)

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CollectionRow(
                    ChooseCollectionSpec(
                        collection = sampleCollection,
                        isSelected = false,
                    )
                )

                CollectionRow(
                    ChooseCollectionSpec(
                        collection = sampleCollection.copy(
                            title = "A very long collection title that should be truncated",
                            description = "A very long collection description that should be truncated",
                            color = CollectionColor.lavender.hex
                        ),
                        isSelected = true,
                    )
                )
            }
        }
    }
}