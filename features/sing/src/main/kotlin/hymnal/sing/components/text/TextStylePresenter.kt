// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.components.text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import hymnal.services.prefs.HymnalPrefs
import hymnal.services.prefs.model.ThemeStyle
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import hymnal.sing.components.text.TextStyleScreen.Event as UiEvent
import hymnal.sing.components.text.TextStyleScreen.State as UiState

@Inject
class TextStylePresenter(
    private val prefs: HymnalPrefs,
) : Presenter<UiState> {
    @Composable
    override fun present(): UiState {
        val coroutineScope = rememberStableCoroutineScope()

        val themeStyle by produceRetainedState(ThemeStyle()) {
            prefs.themeStyle()
                .catch { Timber.e(it) }
                .collect { value = it }
        }

        return UiState(themeStyle) { event ->
            when (event) {
                is UiEvent.OnThemeChange -> {
                    coroutineScope.launch {
                        prefs.updateThemeStyle(themeStyle.copy(theme = event.theme))
                    }
                }

                is UiEvent.OnFontChange -> {
                    coroutineScope.launch {
                        prefs.updateThemeStyle(themeStyle.copy(font = event.font))
                    }
                }

                is UiEvent.OnTextSizeChange -> {
                    coroutineScope.launch {
                        prefs.updateThemeStyle(themeStyle.copy(textSize = event.textSize))
                    }
                }
            }

        }
    }

    @CircuitInject(TextStyleScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(): TextStylePresenter
    }
}