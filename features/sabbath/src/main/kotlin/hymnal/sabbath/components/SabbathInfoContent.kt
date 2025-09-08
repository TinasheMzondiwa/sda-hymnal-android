// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.sabbath.State
import hymnal.ui.theme.HymnalTheme

fun LazyListScope.sabbathInfo(state: State.SabbathInfo) {
    item("location") {
        Row(
            modifier = Modifier
                .animateItem()
                .padding(top = 16.dp)
                .padding(horizontal = 20.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape((24.dp))
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Text(
                text = state.location,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }

    item("sabbath_info") {
        SabbathInfoContent(state = state)
    }
}

@Composable
private fun SabbathInfoContent(state: State.SabbathInfo, modifier: Modifier = Modifier) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isSabbath) {
                Text(
                    text = "It's Sabbath! Enjoy your day of rest.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center,
                )
            }

            Text(
                text = if (state.isSabbath) "Sabbath ends in" else "Next Sabbath begins in",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )

            Text(
                text = state.countDown,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.size(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SabbathTimeItem(
                    label = "Start",
                    value = state.sabbathStart,
                )

                CustomDividerWithCircle(
                    modifier = Modifier.weight(1f),
                )

                SabbathTimeItem(
                    label = "End",
                    value = state.sabbathEnd,
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SabbathTimeItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(imageVector = Icons.Rounded.WbTwilight, contentDescription = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmallEmphasized,
            textAlign = TextAlign.Center,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmallEmphasized.copy(
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CustomDividerWithCircle(modifier: Modifier = Modifier) {
    val lineHeight = 2.dp
    val circleRadius = 8.dp
    val color = DividerDefaults.color

    Canvas(
        modifier = modifier
            .height(lineHeight + circleRadius)
    ) {
        val centerY = size.height / 2f
        val startX = 0f
        val endX = size.width
        val centerX = size.width / 2f

        // Draw the line
        drawLine(
            color = color,
            start = Offset(x = startX, y = centerY),
            end = Offset(x = endX, y = centerY),
            strokeWidth = lineHeight.toPx(),
            cap = StrokeCap.Round
        )

        // Draw the circle
        drawCircle(
            color = color,
            radius = circleRadius.toPx() / 2,
            center = Offset(x = centerX, y = centerY)
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            SabbathInfoContent(
                state = State.SabbathInfo(
                    location = "Harare, Zimbabwe",
                    isSabbath = false,
                    countDown = "1d 3h 05m",
                    sabbathStart = "Friday, 6:00 PM",
                    sabbathEnd = "Saturday, 7:00 PM",
                ),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSabbath() {
    HymnalTheme {
        Surface {
            SabbathInfoContent(
                state = State.SabbathInfo(
                    location = "Toronto, Ontario",
                    isSabbath = true,
                    countDown = "3h 05m",
                    sabbathStart = "Friday, 6:30 PM",
                    sabbathEnd = "Saturday, 7:00 PM",
                ),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}