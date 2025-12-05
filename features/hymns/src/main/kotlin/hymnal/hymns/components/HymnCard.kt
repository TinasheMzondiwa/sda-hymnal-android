// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import hymnal.hymns.SortType
import hymnal.libraries.navigation.key.HymnSharedTransitionKey
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.hymn.NumberText

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
                .padding(vertical = 8.dp),
        ) {
            CardContent(
                hymn = hymn,
                sortType = sortType,
                modifier = Modifier
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedElementTransitionScope.CardContent(
    hymn: Hymn,
    sortType: SortType,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
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
        },
        modifier = modifier,
        leadingContent = {
            AnimatedVisibility(visible = sortType == SortType.NUMBER) {
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
        },
        supportingContent = {
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
        },
        trailingContent = {
            AnimatedVisibility(visible = sortType == SortType.TITLE) {
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
        }
    )
}

internal val previewHymn = Hymn(
    index = "108",
    number = 108,
    title = "Amazing Grace",
    majorKey = null,
    author = "John Newton",
    authorLink = null,
    year = "1985",
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

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    PreviewSharedElementTransitionLayout {
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
}