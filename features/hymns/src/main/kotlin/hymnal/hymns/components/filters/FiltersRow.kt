// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.hymns.SortType
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import hymnal.libraries.l10n.R as L10nR

@Composable
fun FiltersRow(
    items: ImmutableList<FilterItem>,
    onItemClick: (FilterItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalAppHapticFeedback.current

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            FilterChip(
                selected = item.selected,
                onClick = {
                    hapticFeedback.performClick()
                    onItemClick(item)
                },
                label = {
                    val label = when (item) {
                        is FilterItem.Hymnal -> item.title
                        is FilterItem.Sort -> stringResource(item.titleRes)
                    }
                    Text(text = label)
                },
                modifier = Modifier.animateItem(),
                leadingIcon = {
                    if (item is FilterItem.Sort) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Sort,
                            contentDescription = null,
                            tint = if (item.selected) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = null,
                    )
                },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            FiltersRow(
                items = persistentListOf(
                    FilterItem.Hymnal(
                        title = "1985",
                        selected = false,
                    ),
                    FilterItem.Sort(
                        titleRes = L10nR.string.sort_number,
                        selected = true,
                        leadingIcon = SortType.NUMBER.icon,
                    ),
                    FilterItem.Sort(
                        titleRes = L10nR.string.sort_title,
                        selected = false,
                        leadingIcon = SortType.TITLE.icon,
                    ),
                ),
                onItemClick = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}