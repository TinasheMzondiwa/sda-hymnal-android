// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.hymns.SortType
import hymnal.libraries.model.Hymnal
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.hymns.R as HymnsR
import hymnal.libraries.l10n.R as L10nR

@Composable
internal fun ChooseHymnalOverlayContent(
    selected: Hymnal,
    onResult: (Hymnal) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalAppHapticFeedback.current

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(L10nR.string.hymnal),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier,
            fontWeight = FontWeight.SemiBold,
        )

        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(Hymnal.entries) { hymnal ->
                val isSelected = hymnal == selected
                OutlinedCard(
                    onClick = {
                        hapticFeedback.performClick()
                        onResult(hymnal)
                    },
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Unspecified
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(HymnsR.drawable.ic_globe_book),
                                contentDescription = null,
                                modifier = Modifier,
                                tint = MaterialTheme.colorScheme.secondaryContainer
                            )
                        }

                        Text(
                            text = hymnal.title,
                            modifier = Modifier,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            ChooseHymnalOverlayContent(
                selected = Hymnal.NewHymnal,
                onResult = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
internal fun ChooseSortOverlayContent(
    selected: SortType,
    hymns: Int,
    onResult: (SortType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalAppHapticFeedback.current
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(L10nR.string.sort),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier,
            fontWeight = FontWeight.SemiBold,
        )

        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(SortType.entries) { sortType ->
                val isSelected = sortType == selected
                OutlinedCard(
                    onClick = {
                        hapticFeedback.performClick()
                        onResult(sortType)
                    },
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Unspecified
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(sortType.icon),
                                contentDescription = null,
                                modifier = Modifier,
                                tint = MaterialTheme.colorScheme.secondaryContainer
                            )
                        }

                        Text(
                            text = LocalResources.current.getString(sortType.desc, hymns),
                            modifier = Modifier,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SortPreview() {
    HymnalTheme {
        Surface {
            ChooseSortOverlayContent(
                selected = SortType.TITLE,
                hymns = 695,
                onResult = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

