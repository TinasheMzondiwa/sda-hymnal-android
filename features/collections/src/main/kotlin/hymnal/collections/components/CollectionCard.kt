// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import hymnal.services.model.HymnsCollection
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.color.toColor
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun CollectionCard(
    collection: HymnsCollection,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    onDelete: () -> Unit = { },
) {
    val hapticFeedback = LocalAppHapticFeedback.current
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = collection.title,
                    modifier = Modifier,
                )
            },
            modifier = Modifier,
            supportingContent = {
                Column(
                    modifier = Modifier
                ) {
                    collection.description?.takeUnless { it.isEmpty() }?.let {
                        Text(
                            text = it,
                            modifier = Modifier,
                        )
                    }

                    val count = collection.hymns.size
                    if (count > 0) {
                        Text(
                            text = pluralStringResource(
                                id = L10nR.plurals.hymns_count,
                                count = count,
                                count,
                            ),
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(24.dp)
                        .background(collection.color.toColor(), CircleShape)
                )
            },
            trailingContent = {
                var showMenu by remember { mutableStateOf(false) }

                IconButton(
                    onClick = {
                        hapticFeedback.performClick()
                        showMenu = true
                    },
                    modifier = Modifier,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.04f)
                    ),
                ) {
                    Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
                }

                DropdownMenu(
                    expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteForever,
                                contentDescription = null,
                            )
                            Text(stringResource(id = L10nR.string.delete))
                        }
                    }, onClick = {
                        showMenu = false
                        onDelete()
                    })
                }
            })
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun CollectionCardPreview() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            Surface {
                CollectionCard(
                    collection = HymnsCollection(
                        collectionId = "my_collection",
                        title = "My Collection",
                        description = "A collection of my favorite hymns",
                        hymns = listOf(),
                        created = 0L,
                        color = CollectionColor.entries.random().hex
                    ),
                    modifier = Modifier.padding(16.dp),
                    onClick = {},
                )
            }
        }
    }

}