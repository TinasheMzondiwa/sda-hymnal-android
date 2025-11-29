// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components.info

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
data class ResourceItem(
    val resource: SabbathResource,
    val onCitationClick: (String) -> Unit,
) : SabbathInfoItem {
    override val id: String
        get() = when (resource) {
            is SabbathResource.Quote -> resource.id
            is SabbathResource.Scripture -> resource.id
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
            section = (resource as? SabbathResource.Scripture)?.section,
            colors = colors,
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onCitationClick = onCitationClick,
        )
    }
}

@Composable
private fun ResourceCard(
    reference: String,
    text: String,
    section: String?,
    colors: SabbathColors,
    modifier: Modifier = Modifier,
    onCitationClick: (String) -> Unit
) {
    var maxLines by remember { mutableIntStateOf(3) }

    Card(
        modifier = modifier.clickable {
            maxLines = if (maxLines == 3) Int.MAX_VALUE else 3
        },
        colors = CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
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
            section?.let {
                Text(
                    text = it,
                    fontSize = 20.sp,
                    color = colors.text,
                    fontFamily = GaraMond,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                )

                Spacer(Modifier.height(10.dp))
            }

            Text(
                text = reference,
                fontSize = 24.sp,
                color = colors.text,
                fontFamily = GaraMond,
                fontWeight = FontWeight.Medium,
            )

            Spacer(Modifier.height(6.dp))

            ResourceText(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 22.sp,
                color = colors.textSecondary,
                linkColor = colors.accent,
                verseColor = colors.text,
                fontFamily = GaraMond,
                maxLines = maxLines,
                onCitationClick = onCitationClick
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
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ResourceCard(
                        reference = "Nehemiah 13:15-19",
                        text = "[15] In those days saw I in Judah some treading wine presses on the sabbath, and bringing in sheaves, and lading asses; as also wine, grapes, and figs, and all manner of burdens, which they brought into Jerusalem on the sabbath day: and I testified against them in the day wherein they sold victuals. [16] There dwelt men of Tyre also therein, which brought fish, and all manner of ware, and sold on the sabbath unto the children of Judah, and in Jerusalem. [17] Then I contended with the nobles of Judah, and said unto them, What evil thing is this that ye do, and profane the sabbath day? [18] Did not your fathers thus, and did not our God bring all this evil upon us, and upon this city? yet ye bring more wrath upon Israel by profaning the sabbath. [19] And it came to pass, that when the gates of Jerusalem began to be dark before the sabbath, I commanded that the gates should be shut, and charged that they should not be opened till after the sabbath: and some of my servants set I at the gates, that there should no burden be brought in on the sabbath day.",
                        section = "Nehemiah rebukes those trading on the Sabbath and orders the gates of Jerusalem shut during the holy day.",
                        colors = colors,
                        modifier = Modifier.padding(6.dp),
                        onCitationClick = {}
                    )
                }

                item {
                    ResourceCard(
                        reference = "Testimonies for the Church",
                        text = "True reform includes returning to heartfelt obedience to God’s commandments, including the Sabbath. {1T 34.5}",
                        section = null,
                        colors = colors,
                        modifier = Modifier.padding(6.dp),
                        onCitationClick = {}
                    )
                }
            }
        }
    }
}
