// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.R as UiR

@Composable
internal fun SabbathCard(
    bigCountdown: String,
    sabbathStart: String,
    sabbathEnd: String,
    isSabbath: Boolean,
    progress: Float,
    colors: SabbathColors,
    modifier: Modifier = Modifier,
) {
    val shimmer by rememberInfiniteTransition().animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse)
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.35f to colors.gradientTop,
                        1f to colors.gradientBottom
                    )
                )
                .padding(horizontal = 18.dp, vertical = 22.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isSabbath) {
                    Text(
                        text = "It's Sabbath! Enjoy your day of rest.",
                        color = colors.textSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                    )
                }

                Text(
                    text = if (isSabbath) "Sabbath ends in" else "Next Sabbath begins in",
                    color = colors.textSecondary,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                GlowingText(
                    text = bigCountdown,
                    colors = colors,
                    intensity = shimmer,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(22.dp))
                SunsetProgress(progress = progress, height = 10.dp, colors = colors)
                Spacer(Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SunsetTime(
                        label = "Start",
                        time = sabbathStart,
                        colors = colors,
                        alignedEnd = false,
                    )
                    SunsetTime(
                        label = "End",
                        time = sabbathEnd,
                        colors = colors,
                        alignedEnd = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun SunsetTime(
    label: String,
    time: String,
    colors: SabbathColors,
    alignedEnd: Boolean,
) {
    Column(
        horizontalAlignment = if (alignedEnd) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(UiR.drawable.ic_sunset_fill),
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = label,
                color = colors.accent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = time,
            color = colors.text,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun SunsetProgress(
    progress: Float,
    height: Dp,
    colors: SabbathColors,
) {
    val radius = height / 2
    val thumb = 16.dp

    // Pull colors from the theme palette
    val trackColor = colors.trackNeutral
    val fillGradient = Brush.horizontalGradient(listOf(colors.accent, colors.accentDeep))
    val thumbBorder = colors.accent
    val trackOutline = colors.outline
    val thumbSurface = colors.thumbSurface

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(height + 12.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(radius))
        ) {
            // Background track
            drawRoundRect(
                color = trackColor, cornerRadius = CornerRadius(size.height / 2, size.height / 2)
            )

            // Progress fill
            val progW = (size.width * progress.coerceIn(0f, 1f))
            drawRoundRect(
                brush = fillGradient,
                size = androidx.compose.ui.geometry.Size(progW, size.height),
                cornerRadius = CornerRadius(size.height / 2, size.height / 2)
            )

            // Subtle border (neutral)
            drawRoundRect(
                color = trackOutline,
                style = Stroke(width = 2f),
                cornerRadius = CornerRadius(size.height / 2, size.height / 2)
            )
        }

        // Thumb position based on available width minus thumb size
        val available = maxWidth - thumb
        val x by animateFloatAsState(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        val offsetX = available * x

        // Thumb
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 8.dp)
        ) {
            // glow underlay (accent)
            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .size(thumb)
                    .clip(CircleShape)
                    .background(thumbBorder)
                    .blur(8.dp, BlurredEdgeTreatment.Unbounded)
            )
            // visible thumb
            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .size(thumb)
                    .clip(CircleShape)
                    .background(thumbSurface)
                    .border(2.dp, thumbBorder, CircleShape)
            )
        }
    }
}

// ---- Effects / helpers

@Composable
private fun GlowingText(
    text: String,
    colors: SabbathColors,
    intensity: Float,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        // Glow underlay
        Text(
            text = text,
            color = colors.accent.copy(alpha = 0.55f * intensity),
            fontSize = fontSize,
            fontWeight = fontWeight,
            modifier = Modifier.blur(12.dp, BlurredEdgeTreatment.Unbounded)
        )
        // Foreground text
        Text(
            text = text, color = colors.accent, fontSize = fontSize, fontWeight = fontWeight
        )
    }
}

internal fun Modifier.drawRadialGlow(color: Color) = drawBehind {
    val r = (size.minDimension / 2f)
    drawCircle(
        brush = Brush.radialGradient(listOf(color, Color.Transparent), center = center, radius = r),
        radius = r,
        center = center
    )
}

@PreviewLightDark
@Composable
private fun SabbathCardPreview() {
    HymnalTheme {
        val colors = rememberSabbathColors(isDark = isSystemInDarkTheme())
        Surface {
            SabbathCard(
                bigCountdown = "12:34:56",
                sabbathStart = "Fri 7:45 PM",
                sabbathEnd = "Sat 8:56 PM",
                isSabbath = true,
                progress = 0.65f,
                colors = colors,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
    }
}
