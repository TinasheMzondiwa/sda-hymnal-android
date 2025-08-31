// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.navigation.number

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import kotlinx.parcelize.Parcelize
import hymnal.libraries.l10n.R as L10nR
import hymnal.libraries.navigation.number.PadContentScreen.Event as UiEvent
import hymnal.libraries.navigation.number.PadContentScreen.State as UiState

@Parcelize
data object PadContentScreen : Screen {
    data class State(
        val input: String,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data class OnNumberClicked(val number: Int) : Event
        data object OnBackspaceClicked : Event
        data object OnConfirm : Event
    }
}

@Parcelize
data class NumPadPopResult(
    val number: Int,
) : PopResult

@Inject
class PadContentPresenter(@Assisted private val navigator: Navigator) :
    Presenter<UiState> {

    @Composable
    override fun present(): UiState {
        var input by rememberRetained { mutableStateOf("") }

        return UiState(
            input = input,
            eventSink = { event ->
                when (event) {
                    is UiEvent.OnNumberClicked -> {
                        input = validateInput(input + event.number.toString())
                    }

                    UiEvent.OnBackspaceClicked -> {
                        input = if (input.isNotEmpty()) {
                            input.dropLast(1)
                        } else {
                            ""
                        }
                    }

                    UiEvent.OnConfirm -> {
                        navigator.pop(NumPadPopResult(input.toInt()))
                    }
                }
            }
        )
    }


    private fun validateInput(input: String): String {
        return input
            .filter { it.isDigit() }
            .trimStart { it == '0' }
            .take(3)
            .let { if (it.isNotEmpty() && it.toInt() > 695) "695" else it }
    }

    @AssistedFactory
    @CircuitInject(PadContentScreen::class, AppScope::class)
    interface Factory {
        fun create(navigator: Navigator): PadContentPresenter
    }
}

@CircuitInject(PadContentScreen::class, AppScope::class)
@Composable
fun PadContentUi(
    state: UiState,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalAppHapticFeedback.current
    val onNumberClick: (Int) -> Unit = { number ->
        hapticFeedback.performClick()
        state.eventSink(UiEvent.OnNumberClicked(number))
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.size(48.dp))
            Text(
                text = state.input,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp
                )
            )

            AnimatedVisibility(
                visible = state.input.isNotEmpty(),
                modifier = Modifier.sizeIn(48.dp, minHeight = 48.dp)
            ) {
                IconButton(
                    onClick = {
                        hapticFeedback.performError()
                        state.eventSink(UiEvent.OnBackspaceClicked)
                    },
                    enabled = state.input.isNotEmpty(),
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Backspace,
                        contentDescription = null
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            NumberButton(onClick = { onNumberClick(1) }, number = 1, Modifier.weight(1f))
            NumberButton(onClick = { onNumberClick(2) }, number = 2, Modifier.weight(1f))
            NumberButton(onClick = { onNumberClick(3) }, number = 3, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            NumberButton(onClick = { onNumberClick(4) }, number = 4, Modifier.weight(1f))
            NumberButton(onClick = { onNumberClick(5) }, number = 5, Modifier.weight(1f))
            NumberButton(onClick = { onNumberClick(6) }, number = 6, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            NumberButton(onClick = { onNumberClick(7) }, number = 7, Modifier.weight(1f))
            NumberButton(onClick = { onNumberClick(8) }, number = 8, Modifier.weight(1f))
            NumberButton(onClick = { onNumberClick(9) }, number = 9, Modifier.weight(1f))
        }

        NumberButton(
            onClick = { onNumberClick(0) },
            number = 0,
            enabled = state.input.isNotEmpty(),
        )

        FilledTonalButton(onClick = {
            hapticFeedback.performSuccess()
            state.eventSink(UiEvent.OnConfirm)
        }, enabled = state.input.isNotEmpty()) {
            Text(
                text = stringResource(L10nR.string.submit),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun NumberButton(
    onClick: () -> Unit,
    number: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            PadContentUi(
                state = UiState("25", {}),
                modifier = Modifier
            )
        }
    }
}