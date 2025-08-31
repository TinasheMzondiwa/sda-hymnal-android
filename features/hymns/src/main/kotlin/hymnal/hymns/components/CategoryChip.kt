// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.InputChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.ui.theme.HymnalTheme

@Composable
internal fun CategoryChip(
    category: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    InputChip(
        selected = selected,
        onClick = onClick,
        label = { Text(category) },
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryChip(
                    category = "All",
                    selected = true,
                    onClick = {}
                )

                CategoryChip(
                    category = "Holy Spirit",
                    selected = false,
                    onClick = {}
                )
            }
        }
    }
}