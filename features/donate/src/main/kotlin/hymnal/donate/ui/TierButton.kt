// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.ui.theme.HymnalTheme

@Composable
fun TierButton(
    onClick: () -> Unit,
    spec: TierButtonSpec,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderWidth by animateDpAsState(targetValue = if (selected) 1.dp else 0.5.dp)
    val containerColor by animateColorAsState(targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer)
    val contentColor by animateColorAsState(targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer)

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(width = borderWidth, color = contentColor),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = spec.formattedPrice,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Immutable
data class TierButtonSpec(
    val sku: String,
    val formattedPrice: String,
)

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            TierButton(
                onClick = {},
                spec = TierButtonSpec(
                    sku = "123",
                    formattedPrice = "10.00",
                ),
                selected = false,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSelected() {
    HymnalTheme {
        Surface {
            TierButton(
                onClick = {},
                spec = TierButtonSpec(
                    sku = "321",
                    formattedPrice = "24.99",
                ),
                selected = true,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}