// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.ui.theme.HymnalTheme

@Composable
fun TierButton(
    onClick: () -> Unit,
    spec: TierButtonSpec,
    modifier: Modifier = Modifier,
) {

    val borderWidth by animateDpAsState(targetValue = if (spec.selected) 1.dp else 0.5.dp)
    val contentColor by animateColorAsState(targetValue = if (spec.selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor
        ),
        border = BorderStroke(width = borderWidth, color = contentColor)
    ) {
        Text(text = spec.formattedPrice)
    }
}

@Immutable
data class TierButtonSpec(
    val formattedPrice: String,
    val label: String?,
    val selected: Boolean,
)

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            TierButton(
                onClick = {},
                spec = TierButtonSpec(
                    formattedPrice = "10.00",
                    label = null,
                    selected = false
                ),
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
                    formattedPrice = "24.99",
                    label = null,
                    selected = true
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}