// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.more.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.more.InfoLink
import hymnal.ui.theme.HymnalTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun InfoItemsCard(
    section: String,
    items: ImmutableList<InfoLink>,
    onClick: (InfoLink) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = section, style = MaterialTheme.typography.labelLarge)

        OutlinedCard(modifier = Modifier) {
            Column {
                items.forEach { link ->
                    InfoLinkItem(
                        link = link,
                        modifier = Modifier.clickable {
                            onClick(link)
                        },
                    )
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
            InfoItemsCard(
                section = "Support",
                items = persistentListOf(InfoLink.Feedback, InfoLink.ShareApp),
                onClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}