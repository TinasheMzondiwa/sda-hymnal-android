// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import dev.zacsweers.metro.AppScope
import hymnal.collections.components.CollectionColor
import hymnal.collections.components.EmptyCollections
import hymnal.libraries.navigation.AddToCollectionScreen
import hymnal.ui.theme.HymnalTheme
import kotlinx.collections.immutable.persistentListOf
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@CircuitInject(AddToCollectionScreen::class, AppScope::class)
@Composable
fun AddToCollectionUi(state: State, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text(stringResource(L10nR.string.add_to_collection)) },
            actions = {
                Button(
                    onClick = { state.eventSink(Event.CreateNewCollection) },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(L10nR.string.create_collection)
                    )

                    Text(text = stringResource(L10nR.string.create))
                }

                Spacer(Modifier.size(8.dp))
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BottomSheetDefaults.ContainerColor,
                scrolledContainerColor = BottomSheetDefaults.ContainerColor
            )
        )

        when (state) {
            is State.Loading -> {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }

            is State.Empty -> {
                EmptyCollections()

                Button(onClick = { state.eventSink(Event.CreateNewCollection) }) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(stringResource(L10nR.string.create_collection))
                }
            }

            is State.Choose -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
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
                            onClick = { state.eventSink(Event.CollectionSelected(spec)) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PreviewLoading() {
    HymnalTheme {
        Surface(contentColor = BottomSheetDefaults.ContainerColor) {
            AddToCollectionUi(State.Loading {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PreviewEmpty() {
    HymnalTheme {
        Surface(contentColor = BottomSheetDefaults.ContainerColor) {
            AddToCollectionUi(State.Empty {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PreviewChoose() {
    HymnalTheme {
        Surface(contentColor = BottomSheetDefaults.ContainerColor) {
            AddToCollectionUi(
                State.Choose(
                    collections = persistentListOf(
                        ChooseCollectionSpec(
                            collection = sampleCollection,
                            isSelected = false,
                        ),
                        ChooseCollectionSpec(
                            collection = sampleCollection.copy(
                                collectionId = "2",
                                title = "A very long collection title that should be truncated",
                                description = "A very long collection description that should be truncated",
                                color = CollectionColor.lavender.hex
                            ),
                            isSelected = true,
                        )
                    ),
                    eventSink = {}
                )
            )
        }
    }
}