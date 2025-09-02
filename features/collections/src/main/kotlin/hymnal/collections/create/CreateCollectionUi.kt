// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.create

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import dev.zacsweers.metro.AppScope
import hymnal.collections.components.CollectionColor
import hymnal.libraries.navigation.key.AddToCollectionSharedTransitionKey
import hymnal.ui.extensions.modifier.thenIf
import hymnal.ui.extensions.modifier.thenIfNotNull
import hymnal.ui.haptics.AppHapticFeedback
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.color.toColor
import hymnal.collections.create.CreateCollectionScreen.Event as UiEvent
import hymnal.collections.create.CreateCollectionScreen.State as UiState
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@CircuitInject(CreateCollectionScreen::class, AppScope::class)
@Composable
fun CreateCollectionUi(state: UiState, modifier: Modifier = Modifier) {
    val titleState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()
    val hapticFeedback = LocalAppHapticFeedback.current

    SharedElementTransitionScope {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(BottomSheetDefaults.ContainerColor)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CreateCollectionTopAppBar(state, hapticFeedback)

            CreateCollectionContent(state, titleState, descriptionState, hapticFeedback, Modifier)

            SaveButton(state, titleState, descriptionState, hapticFeedback)

            Spacer(Modifier.height(16.dp))
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { titleState.text }.collect {
            state.eventSink(UiEvent.OnTitleChanged(it))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedElementTransitionScope.CreateCollectionTopAppBar(
    state: UiState,
    hapticFeedback: AppHapticFeedback,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(stringResource(L10nR.string.new_collection)) },
        modifier = modifier
            .thenIfNotNull(findAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation)) {
                sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            AddToCollectionSharedTransitionKey(
                                type = AddToCollectionSharedTransitionKey.ElementType.TopAppBar,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
            },
        navigationIcon = {
            if (state.showUpNavigation) {
                IconButton(onClick = {
                    hapticFeedback.performClick()
                    state.eventSink(UiEvent.OnNavigateUp)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(L10nR.string.nav_back)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BottomSheetDefaults.ContainerColor,
            scrolledContainerColor = BottomSheetDefaults.ContainerColor
        )
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedElementTransitionScope.CreateCollectionContent(
    state: UiState,
    titleState: TextFieldState,
    descriptionState: TextFieldState,
    hapticFeedback: AppHapticFeedback,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .thenIfNotNull(findAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation)) {
                sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            AddToCollectionSharedTransitionKey(
                                type = AddToCollectionSharedTransitionKey.ElementType.Content,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
            }
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            state = titleState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            label = { Text(stringResource(L10nR.string.label_title)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        OutlinedTextField(
            state = descriptionState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            label = { Text(stringResource(L10nR.string.label_description)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        ColorPicker(
            selectedColor = state.selectedColor,
            onColorSelected = {
                hapticFeedback.performClick()
                state.eventSink(UiEvent.OnColorSelected(it))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorPicker(
    selectedColor: CollectionColor,
    onColorSelected: (CollectionColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelColor = TextFieldDefaults.colors()
        .labelColor(enabled = true, isError = false, focused = false)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = stringResource(L10nR.string.label_color),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = labelColor,
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CollectionColor.entries) { color ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(32.dp)
                        .background(color.hex.toColor(), CircleShape)
                        .clip(CircleShape)
                        .thenIf(color == selectedColor) {
                            border(
                                width = 1.dp,
                                color = LocalContentColor.current,
                                shape = CircleShape
                            )
                        }
                        .clickable { onColorSelected(color) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (color == selectedColor) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedElementTransitionScope.SaveButton(
    state: UiState,
    titleState: TextFieldState,
    descriptionState: TextFieldState,
    hapticFeedback: AppHapticFeedback,
) {
    Button(
        onClick = {
            hapticFeedback.performSuccess()
            state.eventSink(
                UiEvent.SaveClicked(
                    title = titleState.text,
                    description = descriptionState.text,
                )
            )
        },
        modifier = Modifier
            .thenIfNotNull(findAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation)) {
                sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            AddToCollectionSharedTransitionKey(
                                type = AddToCollectionSharedTransitionKey.ElementType.CreateButton,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        enabled = state.saveEnabled
    ) {
        Text(stringResource(L10nR.string.save))
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            Surface {
                CreateCollectionUi(
                    UiState(
                        showUpNavigation = true,
                        selectedColor = CollectionColor.coralOrange,
                        saveEnabled = true,
                        eventSink = {},
                    )
                )
            }
        }
    }
}