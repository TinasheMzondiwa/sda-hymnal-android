// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.hymns

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import hymnal.collections.components.CollectionColor
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.color.toColor

@Composable
internal fun CollectionDescription(
    description: String,
    color: String,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val borderColor = color.toColor()
    val borderSizeInPx: Float = with(density) { 16.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    strokeWidth = borderSizeInPx,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    cap = StrokeCap.Round
                )
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            Surface {
                CollectionDescription(
                    description =
                        "A collection of hymns that are meant to be sung during evening services and reflect themes of peace, rest, and reflection.",
                    color = CollectionColor.sunriseYellow.hex,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}