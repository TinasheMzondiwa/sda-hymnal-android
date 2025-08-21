package hymnal.sing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hymnal.services.model.HymnLyrics
import hymnal.ui.theme.HymnalTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal fun LazyListScope.hymnLyrics(lyrics: ImmutableList<HymnLyrics>) {
    items(lyrics) { lyric ->
        when (lyric) {
            is HymnLyrics.Chorus -> Chorus(
                lines = lyric.lines.toImmutableList(),
                Modifier.padding(bottom = 24.dp)
            )

            is HymnLyrics.Verse -> Verse(
                index = lyric.index,
                lines = lyric.lines.toImmutableList(),
                Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun Verse(index: Int, lines: ImmutableList<String>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionText(section = "$index")

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            lines.forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                    )
                )
            }
        }
    }
}

@Composable
private fun Chorus(lines: ImmutableList<String>, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val borderColor = MaterialTheme.colorScheme.onSecondaryContainer
    val borderSizeInPx: Float = with(density) { 16.dp.toPx() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.MusicNote,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    .border(0.5.dp, DividerDefaults.color, CircleShape)
                    .padding(8.dp)
            )

            Text(
                text = "Chorus",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(
            modifier = Modifier
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
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            lines.forEach { line ->
                Text(
                    text = line,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                    ),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
internal fun SectionText(
    section: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 32.dp, minHeight = 32.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape,
            )
            .border(0.5.dp, DividerDefaults.color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = section,
            modifier = Modifier.padding(4.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewVerse() {
    HymnalTheme {
        Surface {
            Verse(
                index = 1,
                lines = listOf(
                    "On a hill far away",
                    "Stood an old rugged cross",
                    "The emblem of suffering and shame",
                    "And I love that old cross",
                    "Where the dearest and best",
                    "For a world of lost sinners was slain",
                ).toImmutableList(),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewChorus() {
    HymnalTheme {
        Surface {
            Chorus(
                lines = listOf(
                    "So I'll cherish the old rugged cross",
                    "Till my trophies at last I lay down",
                    "I will cling to the old rugged cross",
                    "And exchange it someday for a crown",
                ).toImmutableList(),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

