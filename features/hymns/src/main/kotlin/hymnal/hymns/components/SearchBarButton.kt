// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import hymnal.libraries.navigation.key.SearchSharedTransitionKey
import hymnal.ui.theme.HymnalTheme
import hymnal.libraries.l10n.R as L10nR


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchBarButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    SharedElementTransitionScope {
        Surface(
            modifier =
                modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(SearchBarDefaults.windowInsets)
                    .semantics { isTraversalGroup = true },
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .sharedElement(
                        sharedContentState =
                            rememberSharedContentState(
                                SearchSharedTransitionKey(
                                    type = SearchSharedTransitionKey.ElementType.Button,
                                )
                            ),
                        animatedVisibilityScope =
                            requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                    )
                    .fillMaxWidth()
                    .padding(start = 6.dp, end = 18.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = SearchBarDefaults.inputFieldShape
                    )
                    .clip(SearchBarDefaults.inputFieldShape)
                    .clickable(onClick = onClick, role = Role.Button)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    modifier = Modifier.sharedBounds(
                        sharedContentState =
                            rememberSharedContentState(
                                SearchSharedTransitionKey(
                                    id = SearchSharedTransitionKey.ICON_ID,
                                    type = SearchSharedTransitionKey.ElementType.Icon,
                                )
                            ),
                        animatedVisibilityScope =
                            requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                    )
                )

                Text(
                    text = stringResource(id = L10nR.string.search_hymnal),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun PreviewSearchButton() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            Surface {
                SearchBarButton(
                    onClick = {},
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}