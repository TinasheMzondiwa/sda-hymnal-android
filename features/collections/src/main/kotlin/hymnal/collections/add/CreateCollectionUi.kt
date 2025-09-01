// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.add

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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import dev.zacsweers.metro.AppScope
import hymnal.collections.components.CollectionColor
import hymnal.ui.extensions.modifier.thenIf
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.color.toColor
import hymnal.collections.add.CreateCollectionScreen.Event as UiEvent
import hymnal.collections.add.CreateCollectionScreen.State as UiState
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(CreateCollectionScreen::class, AppScope::class)
@Composable
fun CreateCollectionUi(state: UiState, modifier: Modifier = Modifier) {
    val titleState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()
    val hapticFeedback = LocalAppHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TopAppBar(
            title = { Text(stringResource(L10nR.string.new_collection)) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BottomSheetDefaults.ContainerColor,
                scrolledContainerColor = BottomSheetDefaults.ContainerColor
            )
        )

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

        Button(
            onClick = {
                state.eventSink(
                    UiEvent.SaveClicked(
                        title = titleState.text,
                        description = descriptionState.text,
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = state.saveEnabled
        ) {
            Text(stringResource(L10nR.string.save))
        }

        Spacer(Modifier.height(16.dp))
    }

    LaunchedEffect(Unit) {
        snapshotFlow { titleState.text }.collect {
            state.eventSink(UiEvent.OnTitleChanged(it))
        }
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


@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            CreateCollectionUi(
                UiState(
                    selectedColor = CollectionColor.coralOrange,
                    saveEnabled = true,
                    eventSink = {},
                )
            )
        }
    }
}