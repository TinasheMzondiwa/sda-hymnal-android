// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.add

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import dev.zacsweers.metro.AppScope
import hymnal.collections.components.CollectionColor
import hymnal.collections.components.EmptyCollections
import hymnal.libraries.navigation.AddToCollectionScreen
import hymnal.libraries.navigation.key.AddToCollectionSharedTransitionKey
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import kotlinx.collections.immutable.persistentListOf
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalSharedTransitionApi::class)
@CircuitInject(AddToCollectionScreen::class, AppScope::class)
@Composable
fun AddToCollectionUi(state: State, modifier: Modifier = Modifier) {
    SharedElementTransitionScope {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AddToCollectionTopBar(state)

            when (state) {
                is State.Loading -> LoadingContent()
                is State.Empty -> EmptyContent(state)
                is State.Choose -> ChooseContent(state, Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedElementTransitionScope.AddToCollectionTopBar(state: State) {
    val hapticFeedback = LocalAppHapticFeedback.current
    TopAppBar(
        title = { Text(stringResource(L10nR.string.add_to_collection)) },
        modifier = Modifier
            .sharedElement(
                sharedContentState =
                    rememberSharedContentState(
                        AddToCollectionSharedTransitionKey(
                            type = AddToCollectionSharedTransitionKey.ElementType.TopAppBar,
                        )
                    ),
                animatedVisibilityScope =
                    requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
            ),
        actions = {
            if (state is State.Choose) {
                Button(
                    onClick = {
                        hapticFeedback.performClick()
                        state.eventSink(Event.CreateNewCollection)
                    },
                    modifier = Modifier.sharedElement(
                        sharedContentState =
                            rememberSharedContentState(
                                AddToCollectionSharedTransitionKey(
                                    type = AddToCollectionSharedTransitionKey.ElementType.CreateButton,
                                )
                            ),
                        animatedVisibilityScope =
                            requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(L10nR.string.create_collection)
                    )

                    Text(text = stringResource(L10nR.string.create))
                }
            }

            Spacer(Modifier.size(8.dp))
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BottomSheetDefaults.ContainerColor,
            scrolledContainerColor = BottomSheetDefaults.ContainerColor
        )
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp), contentAlignment = Alignment.Center
    ) {
        LoadingIndicator()
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedElementTransitionScope.EmptyContent(
    state: State.Empty,
) {
    val hapticFeedback = LocalAppHapticFeedback.current
    EmptyCollections(
        Modifier.sharedElement(
            sharedContentState =
                rememberSharedContentState(
                    AddToCollectionSharedTransitionKey(
                        type = AddToCollectionSharedTransitionKey.ElementType.Content,
                    )
                ),
            animatedVisibilityScope =
                requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
        )
    )

    Button(
        onClick = {
            hapticFeedback.performClick()
            state.eventSink(Event.CreateNewCollection)
        },
        modifier = Modifier.sharedElement(
            sharedContentState =
                rememberSharedContentState(
                    AddToCollectionSharedTransitionKey(
                        type = AddToCollectionSharedTransitionKey.ElementType.CreateButton,
                    )
                ),
            animatedVisibilityScope =
                requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(stringResource(L10nR.string.create_collection))
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedElementTransitionScope.ChooseContent(
    state: State.Choose,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalAppHapticFeedback.current

    LazyColumn(
        modifier = modifier
            .sharedElement(
                sharedContentState =
                    rememberSharedContentState(
                        AddToCollectionSharedTransitionKey(
                            type = AddToCollectionSharedTransitionKey.ElementType.Content,
                        )
                    ),
                animatedVisibilityScope =
                    requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
            ),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(state.collections, key = { it.id }) { spec ->
            CollectionRow(
                spec = spec,
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    hapticFeedback.performClick()
                    state.eventSink(Event.CollectionSelected(spec))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PreviewLoading() {
    Preview {
        AddToCollectionUi(State.Loading {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PreviewEmpty() {
    Preview {
        AddToCollectionUi(State.Empty {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PreviewChoose() {
    Preview {
        AddToCollectionUi(
            State.Choose(
                collections = persistentListOf(
                    ChooseCollectionSpec(
                        collection = sampleCollection,
                        isSelected = false,
                    ), ChooseCollectionSpec(
                        collection = sampleCollection.copy(
                            collectionId = "2",
                            title = "A very long collection title that should be truncated",
                            description = "A very long collection description that should be truncated",
                            color = CollectionColor.lavender.hex
                        ),
                        isSelected = true,
                    )
                ), eventSink = {})
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun Preview(content: @Composable () -> Unit) {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            Surface(contentColor = BottomSheetDefaults.ContainerColor) {
                content()
            }
        }
    }
}
