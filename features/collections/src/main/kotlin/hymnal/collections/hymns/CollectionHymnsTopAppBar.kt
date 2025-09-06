// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.hymns

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import hymnal.libraries.navigation.key.CollectionSharedTransitionKey
import hymnal.libraries.l10n.R as L10nR

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
internal fun CollectionHymnsTopAppBar(state: State, modifier: Modifier = Modifier) {
    SharedElementTransitionScope {
        TopAppBar(
            title = {
                Text(
                    text = state.title,
                    modifier = Modifier.sharedBounds(
                        sharedContentState =
                            rememberSharedContentState(
                                CollectionSharedTransitionKey(
                                    id = state.id,
                                    type = CollectionSharedTransitionKey.ElementType.Title,
                                )
                            ),
                        animatedVisibilityScope =
                            requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            modifier = modifier,
            subtitle = {
                state.description?.let {
                    Text(
                        text = it,
                        modifier = Modifier.sharedBounds(
                            sharedContentState =
                                rememberSharedContentState(
                                    CollectionSharedTransitionKey(
                                        id = state.id,
                                        type = CollectionSharedTransitionKey.ElementType.Description,
                                    )
                                ),
                            animatedVisibilityScope =
                                requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                        )
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { state.eventSink(Event.OnNavBack) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(L10nR.string.nav_back)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {},
                    modifier = Modifier.sharedBounds(
                        sharedContentState =
                            rememberSharedContentState(
                                CollectionSharedTransitionKey(
                                    id = state.id,
                                    type = CollectionSharedTransitionKey.ElementType.MoreButton,
                                )
                            ),
                        animatedVisibilityScope =
                            requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                    ),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.04f)
                    )
                ) {
                    Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            )
        )
    }
}