// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import hymnal.hymns.SortType
import hymnal.libraries.navigation.key.HymnSharedTransitionKey
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics
import hymnal.ui.theme.HymnalTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HymnCard(
    hymn: Hymn,
    sortType: SortType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    SharedElementTransitionScope {
        OutlinedCard(
            onClick = onClick,
            modifier = modifier
                .sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            HymnSharedTransitionKey(
                                id = hymn.index,
                                type = HymnSharedTransitionKey.ElementType.Card,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            AnimatedContent(targetState = sortType) { targetSortType ->
                Row(
                    modifier = modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    if (targetSortType == SortType.NUMBER) {
                        NumberText(
                            number = hymn.number,
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState =
                                        rememberSharedContentState(
                                            HymnSharedTransitionKey(
                                                id = hymn.index,
                                                type = HymnSharedTransitionKey.ElementType.Number,
                                            )
                                        ),
                                    animatedVisibilityScope =
                                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                                )
                                .padding(end = 8.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = hymn.title,
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState =
                                        rememberSharedContentState(
                                            HymnSharedTransitionKey(
                                                id = hymn.index,
                                                type = HymnSharedTransitionKey.ElementType.Title,
                                            )
                                        ),
                                    animatedVisibilityScope =
                                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                                ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp
                            ),
                        )

                        hymn.lyrics.firstOrNull()?.let { verse ->
                            Text(
                                text = verse.lines.take(3).joinToString(separator = "\n"),
                                modifier = Modifier.sharedBounds(
                                    sharedContentState =
                                        rememberSharedContentState(
                                            HymnSharedTransitionKey(
                                                id = hymn.index,
                                                type = HymnSharedTransitionKey.ElementType.Lyrics,
                                            )
                                        ),
                                    animatedVisibilityScope =
                                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    if (targetSortType == SortType.TITLE) {
                        NumberText(
                            number = hymn.number,
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState =
                                        rememberSharedContentState(
                                            HymnSharedTransitionKey(
                                                id = hymn.index,
                                                type = HymnSharedTransitionKey.ElementType.Number,
                                            )
                                        ),
                                    animatedVisibilityScope =
                                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                                )
                        )
                    }

                    IconButton(
                        onClick = {},
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black.copy(
                                alpha = 0.04f
                            )
                        )
                    ) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = null)
                    }
                }

            }
        }
    }
}

@Composable
internal fun NumberText(number: Int, modifier: Modifier = Modifier) {
    val verticalSpacing = if (number >= 100) 8.dp else 4.dp

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 40.dp, minHeight = 40.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape,
            )
            .border(0.5.dp, DividerDefaults.color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = verticalSpacing),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        )
    }
}

internal val previewHymn = Hymn(
    index = "108",
    number = 108,
    title = "Amazing Grace",
    majorKey = null,
    lyrics = listOf(
        HymnLyrics.Verse(
            index = 1,
            lines = listOf(
                "Amazing grace! How sweet the sound",
                "That saved a wretch like me!",
                "I once was lost, but now am found;",
                "Was blind, but now I see.",
            ),
        ),
    ),
)

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    HymnCard(
                        hymn = previewHymn,
                        sortType = SortType.NUMBER,
                        modifier = Modifier
                    )
                }

                item {
                    HymnCard(
                        hymn = previewHymn.copy(number = 8),
                        sortType = SortType.NUMBER,
                        modifier = Modifier
                    )
                }

                item {
                    HymnCard(
                        hymn = previewHymn.copy(number = 99),
                        sortType = SortType.NUMBER,
                        modifier = Modifier
                    )
                }

                item {
                    HymnCard(
                        hymn = previewHymn,
                        sortType = SortType.TITLE,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}