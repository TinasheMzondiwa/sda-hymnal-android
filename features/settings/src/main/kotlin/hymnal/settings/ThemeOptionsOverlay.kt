// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.slack.circuit.overlay.Overlay
import com.slack.circuit.overlay.OverlayNavigator
import hymnal.services.prefs.model.AppTheme
import hymnal.ui.haptics.LocalAppHapticFeedback

class ThemeOptionsOverlay(private val selectedTheme: AppTheme) :
    Overlay<ThemeOptionsOverlay.Result> {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(navigator: OverlayNavigator<Result>) {
        val hapticFeedback = LocalAppHapticFeedback.current

        BasicAlertDialog(
            onDismissRequest = {
                hapticFeedback.performGestureEnd()
                navigator.finish(Result.Dismiss)
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp)) {
                    AppTheme.entries.forEach { theme ->
                        val checked = theme == selectedTheme

                        val onCheckedChange = { checked: Boolean ->
                            hapticFeedback.performToggleSwitch(checked)
                            navigator.finish(Result.Selected(theme))
                        }

                        ListItem(
                            headlineContent = { Text(text = stringResource(theme.label)) },
                            modifier = Modifier
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .toggleable(
                                    value = checked,
                                    role = Role.Switch,
                                    onValueChange = onCheckedChange
                                ),
                            trailingContent = {
                                RadioButton(
                                    selected = checked,
                                    onClick = { onCheckedChange(!checked) }
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    sealed interface Result {
        data object Dismiss : Result
        data class Selected(val theme: AppTheme) : Result
    }
}