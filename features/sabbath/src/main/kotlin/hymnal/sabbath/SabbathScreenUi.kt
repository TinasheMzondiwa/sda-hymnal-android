// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.keepScreenOn
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.SabbathScreen

@CircuitInject(SabbathScreen::class, AppScope::class)
@Composable
fun SabbathScreenUi(state: State, modifier: Modifier = Modifier) {
    Scaffold (
        modifier = modifier.fillMaxSize().keepScreenOn(),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { contentPadding ->
        LazyColumn (contentPadding = contentPadding) {  }
    }
}