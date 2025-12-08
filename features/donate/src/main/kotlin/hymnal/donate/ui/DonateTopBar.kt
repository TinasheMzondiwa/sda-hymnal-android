// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import hymnal.donate.Event
import hymnal.donate.State
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateTopBar(state: State, modifier: Modifier = Modifier) {
    val hapticFeedback = LocalAppHapticFeedback.current

    TopAppBar(
        title = { Text(stringResource(L10nR.string.donate)) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = {
                hapticFeedback.performClick()
                when (state) {
                    is State.Donate -> state.eventSink(Event.OnClose)
                    is State.Loading -> state.eventSink(Event.OnClose)
                }
            }) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        )
    )
}