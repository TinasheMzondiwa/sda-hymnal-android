/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.hymns.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.material3.SearchBarDefaults.inputFieldShape
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
internal fun InputField(
    textFieldState: TextFieldState,
    searchBarState: SearchBarState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    scrollState: ScrollState = rememberScrollState(),
    shape: Shape = inputFieldShape,
    colors: TextFieldColors = inputFieldColors(),
    interactionSource: MutableInteractionSource? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    /*
    Relationship between focus and expansion state:
        * In touch mode, the two are coupled:
            * Text field gains focus => search bar expands
            * Search bar collapses => text field loses focus
        * In non-touch/keyboard mode, they are independent. Instead, expansion triggers when:
            * the user starts typing
            * the user presses the down direction key
     */
    val focused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current
    val isInTouchMode = LocalInputModeManager.current.inputMode == InputMode.Touch

    val searchSemantics = "Search"
    val suggestionsAvailableSemantics = "Suggestions available, press down to see them"

    val textColor = textStyle.color
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    val coroutineScope = rememberCoroutineScope()

    BasicTextField(
        state = textFieldState,
        modifier =
            modifier
                .onPreviewKeyEvent {
                    val expandOnDownKey = !isInTouchMode && !searchBarState.isExpanded
                    if (expandOnDownKey && it.key == Key.DirectionDown) {
                        coroutineScope.launch { searchBarState.animateToExpanded() }
                        return@onPreviewKeyEvent true
                    }
                    // Make sure arrow key down moves to list of suggestions.
                    if (searchBarState.isExpanded && it.key == Key.DirectionDown) {
                        focusManager.moveFocus(FocusDirection.Down)
                        return@onPreviewKeyEvent true
                    }
                    false
                }
                .sizeIn(
                    minWidth = SearchBarMinWidth,
                    maxWidth = SearchBarMaxWidth,
                    minHeight = SearchBarMinHeight,
                )
                .onFocusChanged {
                    if (it.isFocused && isInTouchMode) {
                        coroutineScope.launch { searchBarState.animateToExpanded() }
                    }
                }
                .semantics {
                    contentDescription = searchSemantics
                    if (searchBarState.isExpanded) {
                        stateDescription = suggestionsAvailableSemantics
                    }
                },
        enabled = enabled,
        readOnly = readOnly,
        lineLimits = TextFieldLineLimits.SingleLine,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        onKeyboardAction = { onSearch(textFieldState.text.toString()) },
        interactionSource = interactionSource,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        scrollState = scrollState,
        decorator =
            TextFieldDefaults.decorator(
                state = textFieldState,
                enabled = enabled,
                lineLimits = TextFieldLineLimits.SingleLine,
                outputTransformation = outputTransformation,
                interactionSource = interactionSource,
                placeholder = placeholder,
                leadingIcon =
                    leadingIcon?.let { leading ->
                        { Box(Modifier.offset(x = SearchBarIconOffsetX)) { leading() } }
                    },
                trailingIcon =
                    trailingIcon?.let { trailing ->
                        { Box(Modifier.offset(x = -SearchBarIconOffsetX)) { trailing() } }
                    },
                prefix = prefix,
                suffix = suffix,
                colors = colors,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                container = {
                    val containerColor =
                        animateColorAsState(
                            targetValue =
                                colors.containerColor(
                                    enabled = enabled,
                                    isError = false,
                                    focused = focused,
                                ),
                            animationSpec = tween(),
                        )
                    Box(Modifier.textFieldBackground(containerColor::value, shape))
                },
            ),
    )

    // Most expansions from touch happen via `onFocusChanged` above, but in a mixed
    // keyboard-touch flow, the user can focus via keyboard (with no expansion),
    // and subsequent touches won't change focus state. So this effect is needed as well.
    DetectClickFromInteractionSource(interactionSource) {
        if (!searchBarState.isExpanded) {
            coroutineScope.launch { searchBarState.animateToExpanded() }
        }
    }

    // Expand search bar if the user starts typing
    LaunchedEffect(searchBarState, textFieldState) {
        if (!searchBarState.isExpanded) {
            var prevLength = textFieldState.text.length
            snapshotFlow { textFieldState.text }
                .onEach {
                    val currLength = it.length
                    if (currLength > prevLength && focused && !searchBarState.isExpanded) {
                        // Don't use LaunchedEffect's coroutine because
                        // cancelling the animation shouldn't cancel the Flow
                        coroutineScope.launch { searchBarState.animateToExpanded() }
                    }
                    prevLength = currLength
                }
                .collect { }
        }
    }

    val shouldClearFocusOnCollapse = !searchBarState.isExpanded && focused && isInTouchMode
    LaunchedEffect(searchBarState.isExpanded) {
        if (shouldClearFocusOnCollapse) {
            focusManager.clearFocus()
        }
    }
}

@Composable
private fun DetectClickFromInteractionSource(
    interactionSource: InteractionSource,
    onClick: () -> Unit,
) {
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) onClick()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private val SearchBarState.isExpanded
    get() = this.currentValue == SearchBarValue.Expanded

private fun Modifier.textFieldBackground(color: ColorProducer, shape: Shape): Modifier =
    this.drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        onDrawBehind { drawOutline(outline, color = color()) }
    }

private val SearchBarIconOffsetX: Dp = 4.dp
private val SearchBarMinWidth: Dp = 360.dp
private val SearchBarMaxWidth: Dp = 720.dp
private val SearchBarMinHeight: Dp = 48.dp

