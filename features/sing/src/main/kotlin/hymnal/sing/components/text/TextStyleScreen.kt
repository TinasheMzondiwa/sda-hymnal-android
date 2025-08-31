/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.sing.components.text

import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import hymnal.services.prefs.model.AppFont
import hymnal.services.prefs.model.AppTheme
import hymnal.services.prefs.model.ThemeStyle
import kotlinx.parcelize.Parcelize

@Parcelize
object TextStyleScreen : Screen {

    data class State(
        val style: ThemeStyle,
        val eventSink: (Event) -> Unit
    ) : CircuitUiState

    sealed interface Event {
        data class OnThemeChange(val theme: AppTheme): Event
        data class OnFontChange(val font: AppFont): Event
        data class OnTextSizeChange(val textSize: Float): Event
    }
}

