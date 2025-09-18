// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.hymns

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import hymnal.libraries.navigation.key.HymnSharedTransitionKey
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.hymn.NumberText

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun HymnItem(
    hymn: Hymn,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    SharedElementTransitionScope {
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
                        )
                )
            },
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick),
            leadingContent = {
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
            },
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
                HymnItem(hymn = previewHymn, modifier = Modifier)
            }
        }
    }
}

internal val previewHymn = Hymn(
    index = "108",
    number = 108,
    title = "Amazing Grace",
    majorKey = null,
    author = "John Newton",
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