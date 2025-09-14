// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
fun ThemeCard(
    selected: Boolean,
    darkTheme: Boolean?,
    dynamicColor: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit = {}
) {
    HymnalTheme(darkTheme = darkTheme ?: isSystemInDarkTheme(), dynamicColor = dynamicColor) {
        OutlinedCard(
            modifier = modifier
                .size(width = 80.dp, height = 120.dp)
                .clip(CardDefaults.outlinedShape)
                .toggleable(value = selected, onValueChange = onClick, role = Role.Checkbox),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {

                Spacer(Modifier)

                Spacer(
                    Modifier
                        .size(48.dp, 2.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )

                Spacer(
                    Modifier
                        .size(32.dp, 2.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )

                Spacer(
                    Modifier
                        .size(24.dp, 2.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                RadioButton(
                    selected = selected,
                    onClick = { onClick(!selected) },
                    modifier = Modifier
                )
            }

        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    ThemeCard(
        selected = true,
        darkTheme = true,
        dynamicColor = false,
        modifier = Modifier.padding(16.dp),
        onClick = {}
    )
}