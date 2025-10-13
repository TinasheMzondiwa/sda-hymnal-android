// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.info

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.InfoScreen
import hymnal.ui.widget.scaffold.HazeScaffold

@CircuitInject(InfoScreen::class, AppScope::class)
@Composable
fun InfoScreenUi (state: State, modifier: Modifier = Modifier) {
    HazeScaffold(modifier = modifier) {

    }
}