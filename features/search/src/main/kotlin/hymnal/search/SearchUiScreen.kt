// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.SearchScreen
import hymnal.ui.widget.scaffold.HazeScaffold

@CircuitInject(SearchScreen::class, AppScope::class)
@Composable
fun SearchUiScreen(state: State, modifier: Modifier = Modifier) {
    HazeScaffold(modifier = modifier) { }
}