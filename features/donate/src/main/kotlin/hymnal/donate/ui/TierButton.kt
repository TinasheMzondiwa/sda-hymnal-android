// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate.ui

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

@Composable
fun TierButton(
    spec: TierButtonSpec,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(onClick = spec.onClick, modifier = modifier) {
        Text(text = spec.formattedPrice)
    }
}

@Immutable
data class TierButtonSpec(
    val onClick: () -> Unit,
    val formattedPrice: String,
    val label: String?,
)