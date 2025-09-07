// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.hymns

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import hymnal.libraries.navigation.key.CollectionSharedTransitionKey
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.color.toColor
import hymnal.libraries.l10n.R as L10nR

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
internal fun CollectionHymnsTopAppBar(
    state: State,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val hapticFeedback = LocalAppHapticFeedback.current

    SharedElementTransitionScope {
        LargeTopAppBar(
            title = {
                Column(modifier = Modifier.fillMaxWidth().skipToLookaheadSize()) {
                    Text(
                        text = (state as? State.Content)?.title ?: "",
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState =
                                    rememberSharedContentState(
                                        CollectionSharedTransitionKey(
                                            id = state.id,
                                            type = CollectionSharedTransitionKey.ElementType.Title,
                                        )
                                    ),
                                animatedVisibilityScope =
                                    requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                            )
                            .fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    AnimatedVisibility(scrollBehavior?.state?.collapsedFraction == 0f) {
                        Spacer(
                            Modifier
                                .padding(top = 16.dp)
                                .size(48.dp, 4.dp)
                                .background(
                                    color = (state as? State.Content)?.color?.toColor()
                                        ?: MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            },
            modifier = modifier.skipToLookaheadSize(),
            navigationIcon = {
                IconButton(onClick = {
                    hapticFeedback.performClick()
                    (state as? State.Content)?.eventSink(Event.OnNavBack)
                }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(L10nR.string.nav_back)
                    )
                }
            },
            actions = {
                var showMenu by remember { mutableStateOf(false) }
                IconButton(
                    onClick = {
                        hapticFeedback.performClick()
                        showMenu = true
                    },
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
                DropdownMenu(
                    expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteForever,
                                    contentDescription = null
                                )
                                Text(stringResource(id = L10nR.string.delete))
                            }
                        },
                        onClick = {
                            showMenu = false
                            hapticFeedback.performError()
                            (state as? State.Content)?.eventSink(Event.OnDeleteCollectionClicked)
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
            scrollBehavior = scrollBehavior,
        )
    }
}