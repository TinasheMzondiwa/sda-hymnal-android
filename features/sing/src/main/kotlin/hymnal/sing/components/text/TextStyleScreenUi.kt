// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.components.text

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TextDecrease
import androidx.compose.material.icons.rounded.TextIncrease
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.services.prefs.model.AppFont
import hymnal.services.prefs.model.AppTheme
import hymnal.services.prefs.model.ThemeStyle
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.ThemeCard
import hymnal.ui.theme.type.AdventSansFontFamily
import hymnal.ui.theme.type.GentiumFontFamily
import hymnal.ui.theme.type.LatoFontFamily
import hymnal.ui.theme.type.LoraFontFamily
import hymnal.ui.theme.type.Poppins
import hymnal.ui.theme.type.ProximaFontFamily
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import hymnal.libraries.l10n.R as L10nR
import hymnal.sing.components.text.TextStyleScreen.Event as UiEvent
import hymnal.sing.components.text.TextStyleScreen.State as UiState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@CircuitInject(TextStyleScreen::class, AppScope::class)
@Composable
fun TextStyleScreenUi(state: UiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextSizeSelector(
            selected = state.style.textSize,
            onSelected = { state.eventSink(UiEvent.OnTextSizeChange(it)) }
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 6.dp),
            thickness = 0.5.dp,
        )

        AppFontSelector(
            selectedFont = state.style.font,
            onSelected = { state.eventSink(UiEvent.OnFontChange(it)) }
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 6.dp),
            thickness = 0.5.dp,
        )
        AppThemeSelector(
            selectedTheme = state.style.theme,
            dynamicColors = state.style.dynamicColors,
            onSelected = { theme, dynamicColors ->
                state.eventSink(
                    UiEvent.OnThemeChange(
                        theme,
                        dynamicColors
                    )
                )
            }
        )

        Spacer(
            Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AppThemeSelector(
    selectedTheme: AppTheme,
    dynamicColors: Boolean,
    modifier: Modifier = Modifier,
    onSelected: (AppTheme, Boolean) -> Unit = { theme, colors -> },
) {
    val hapticFeedback = LocalAppHapticFeedback.current

    val options: ImmutableList<Pair<Boolean?, Boolean>> = remember {
        persistentListOf(
            Pair(false, false),
            Pair(false, true),
            Pair(true, false),
            Pair(true, true),
        )
    }
    var selectedPair by rememberSaveable(selectedTheme, dynamicColors) {
        mutableStateOf(selectedTheme.isDarkTheme() to dynamicColors)
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(L10nR.string.settings_theme),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(options) { (darkTheme, dynamicColor) ->
                ThemeCard(
                    selected = selectedPair.first == darkTheme && selectedPair.second == dynamicColor,
                    darkTheme = darkTheme,
                    dynamicColor = dynamicColor,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = {
                        selectedPair = darkTheme to dynamicColor
                        hapticFeedback.performToggleSwitch(it)
                        onSelected(
                            when (darkTheme) {
                                null -> AppTheme.FOLLOW_SYSTEM
                                true -> AppTheme.DARK
                                false -> AppTheme.LIGHT
                            },
                            dynamicColor
                        )
                    }
                )
            }
        }
    }
}

private fun AppTheme.isDarkTheme(): Boolean? = when (this) {
    AppTheme.FOLLOW_SYSTEM -> null
    AppTheme.LIGHT -> false
    AppTheme.DARK -> true
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@SuppressLint("DeprecatedCall")
private fun AppFontSelector(
    selectedFont: AppFont,
    modifier: Modifier = Modifier,
    onSelected: (AppFont) -> Unit = {},
) {
    val hapticFeedback = LocalAppHapticFeedback.current
    Column(modifier = modifier) {
        Text(
            text = stringResource(L10nR.string.settings_typeface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        FlowRow(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            AppFont.entries.forEachIndexed { index, font ->
                ToggleButton(
                    checked = selectedFont == font,
                    onCheckedChange = {
                        if (it) {
                            hapticFeedback.performToggleSwitch(true)
                            onSelected(font)
                        }
                    },
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            AppFont.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                    modifier = Modifier.semantics { role = Role.RadioButton },
                ) {
                    Text(
                        text = font.label,
                        fontFamily = font.toFamily(),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextSizeSelector(
    selected: Float,
    onSelected: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalAppHapticFeedback.current
    var sliderPosition by rememberSaveable(selected) { mutableFloatStateOf(selected) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(L10nR.string.settings_text_size),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Rounded.TextDecrease, contentDescription = null)
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    hapticFeedback.performGestureEnd()
                    onSelected(it)
                },
                modifier = Modifier.weight(1f),
                steps = 5,
                valueRange = MIN_FONT_SIZE..MAX_FONT_SIZE,
            )

            Icon(
                imageVector = Icons.Rounded.TextIncrease,
                contentDescription = null,
            )
        }
    }
}

internal fun AppFont.toFamily(): FontFamily = when (this) {
    AppFont.POPPINS -> Poppins
    AppFont.LATO -> LatoFontFamily
    AppFont.PROXIMA_NOVA -> ProximaFontFamily
    AppFont.ANDADA -> LoraFontFamily
    AppFont.GENTIUM_BOOK -> GentiumFontFamily
    AppFont.ADVENT_SANS -> AdventSansFontFamily
}

private const val MIN_FONT_SIZE = 12f
private const val MAX_FONT_SIZE = 30f

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            TextStyleScreenUi(state = UiState(ThemeStyle()) {})
        }
    }
}