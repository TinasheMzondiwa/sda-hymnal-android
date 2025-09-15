// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.immersive

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImmersiveContentScreen(val hymnId: String): Screen {
    data class State(val pages: ImmutableList<ContentPage>) : CircuitUiState
}

@Immutable
data class ContentPage(
    val lines: ImmutableList<String>,
)
