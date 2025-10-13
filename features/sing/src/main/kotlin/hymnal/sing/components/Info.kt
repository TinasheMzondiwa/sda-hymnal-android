// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import hymnal.libraries.navigation.key.HymnSharedTransitionKey
import hymnal.sing.components.model.TextStyleSpec
import hymnal.sing.components.text.toFamily
import hymnal.ui.theme.HymnalTheme
import kotlinx.collections.immutable.persistentListOf
import hymnal.libraries.l10n.R as L10nR

internal fun LazyListScope.hymnInfo(
    hymn: HymnContent,
    textStyle: TextStyleSpec,
) {
    item(key = "hymn_info_${hymn.index}") {
        HymnInfo(
            hymn = hymn,
            textStyle = textStyle,
            modifier = Modifier.animateItem(),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun HymnInfo(
    hymn: HymnContent,
    textStyle: TextStyleSpec,
    modifier: Modifier = Modifier,
) {
    SharedElementTransitionScope {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {

            Text(
                text = "${hymn.number}",
                modifier = Modifier.sharedBounds(
                    sharedContentState =
                        rememberSharedContentState(
                            HymnSharedTransitionKey(
                                id = hymn.index,
                                type = HymnSharedTransitionKey.ElementType.Number,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                ),
                style = MaterialTheme.typography.headlineMediumEmphasized.copy(
                    fontFamily = textStyle.font.toFamily(),
                    fontSize = (textStyle.textSize * 1.5).sp,
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = hymn.title,
                modifier = Modifier.sharedBounds(
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
                style = MaterialTheme.typography.headlineMediumEmphasized.copy(
                    fontFamily = textStyle.font.toFamily(),
                    fontSize = (textStyle.textSize * 1.5).sp,
                ),
                textAlign = TextAlign.Center
            )

             hymn.author?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmallEmphasized.copy(
                        fontFamily = textStyle.font.toFamily(),
                        fontSize = (textStyle.textSize * 0.8).sp,
                        fontStyle = FontStyle.Italic,
                    ),
                    textAlign = TextAlign.Center
                )
            }

            hymn.majorKey?.let {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(L10nR.string.prefix_major_key))
                        append(" ")
                        pushStyle(
                            MaterialTheme.typography.bodyMediumEmphasized.toSpanStyle().copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = textStyle.font.toFamily(),
                                fontStyle = FontStyle.Normal,
                                fontSize = textStyle.textSize.sp,
                            )
                        )
                        append(it)
                        pop()
                    },
                    style = MaterialTheme.typography.bodySmallEmphasized.copy(
                        fontFamily = textStyle.font.toFamily(),
                        fontSize = textStyle.textSize.sp,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            Surface {
                HymnInfo(
                    hymn = HymnContent(
                        index = "108",
                        number = 108,
                        title = "Amazing Grace",
                        majorKey = "C",
                        author = "John Newton",
                        lyrics = persistentListOf(),
                    ),
                    textStyle = TextStyleSpec(),
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }
    }
}