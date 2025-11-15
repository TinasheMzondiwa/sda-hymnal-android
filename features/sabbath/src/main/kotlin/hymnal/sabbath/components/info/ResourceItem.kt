// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components.info

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hymnal.libraries.model.SabbathResource
import hymnal.sabbath.components.SabbathColors
import hymnal.sabbath.components.SabbathInfoItem
import hymnal.sabbath.components.rememberSabbathColors
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.type.GaraMond

@Immutable
data class ResourceItem(val resource: SabbathResource) : SabbathInfoItem {
    override val id: String
        get() = when (resource) {
            is SabbathResource.Quote -> "egw.${resource.id}"
            is SabbathResource.Scripture -> "scripture.${resource.id}"
        }

    @Composable
    override fun Content(
        colors: SabbathColors,
        modifier: Modifier
    ) {
        ResourceCard(
            reference = when (resource) {
                is SabbathResource.Quote -> resource.reference
                is SabbathResource.Scripture -> resource.reference
            },
            text = when (resource) {
                is SabbathResource.Quote -> resource.text
                is SabbathResource.Scripture -> resource.text
            },
            colors = colors,
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun ResourceCard(
    reference: String,
    text: String,
    colors: SabbathColors,
    modifier: Modifier = Modifier
) {
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
            Text(
                text = buildAnnotatedString {
                    appendLine("\"$text\"")
                    withStyle(
                        style = SpanStyle(
                            fontStyle = FontStyle.Italic,
                            color = colors.text,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(" - ")
                        append(reference)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                fontSize = 22.sp,
                color = colors.textSecondary,
                fontFamily = GaraMond
            )
        }

    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    val colors = rememberSabbathColors(isDark = isSystemInDarkTheme())

    HymnalTheme {
        Surface {
            ResourceCard(
                reference = "Testimonies for the Church",
                text = "True reform includes returning to heartfelt obedience to God’s commandments, including the Sabbath.",
                colors = colors,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
