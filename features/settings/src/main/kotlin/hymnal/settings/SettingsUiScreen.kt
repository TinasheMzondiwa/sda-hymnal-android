// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.SettingsScreen
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.libraries.l10n.R as L10nR
import hymnal.settings.R as SettingsR

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(SettingsScreen::class, AppScope::class)
@Composable
fun SettingsUiScreen(state: State, modifier: Modifier = Modifier) {
    val hapticFeedback = LocalAppHapticFeedback.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(L10nR.string.settings),
            style = MaterialTheme.typography.labelLarge,
        )

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {

            val localCheckedChange = remember {
                { checked: Boolean ->
                    hapticFeedback.performToggleSwitch(checked)

                    state.eventSink(Event.OnToggleLastViewedHymn(checked))
                }
            }

            ListItem(
                headlineContent = {
                    Text(text = stringResource(L10nR.string.pref_last_hymn_title))
                },
                modifier = Modifier
                    .toggleable(
                        value = state.openLastViewedHymn,
                        role = Role.Switch,
                        onValueChange = localCheckedChange
                    ),
                supportingContent = {
                    Text(text = stringResource(L10nR.string.pref_last_hymn_desc))
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(SettingsR.drawable.ic_beenhere),
                        contentDescription = null
                    )
                },
                trailingContent = {
                    Switch(
                        checked = state.openLastViewedHymn,
                        onCheckedChange = localCheckedChange
                    )
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            SettingsUiScreen(
                state = State(
                    openLastViewedHymn = true,
                    eventSink = {},
                ),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}