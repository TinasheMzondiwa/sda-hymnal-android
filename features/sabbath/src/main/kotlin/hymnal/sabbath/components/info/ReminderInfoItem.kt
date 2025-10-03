// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components.info

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import hymnal.sabbath.Event
import hymnal.sabbath.components.SabbathColors
import hymnal.sabbath.components.SabbathInfoItem
import hymnal.sabbath.components.rememberSabbathColors
import hymnal.ui.theme.HymnalTheme

@Immutable
data class ReminderInfoItem(
    val enabled: Boolean,
    val eventSink: (Event.SabbathInfo) -> Unit,
) : SabbathInfoItem {

    override val id: String = "reminder"

    @Composable
    override fun Content(colors: SabbathColors, modifier: Modifier) {
        val context = LocalContext.current

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                eventSink(Event.SabbathInfo.OnReminderToggled(true))
            } else {
                eventSink(Event.SabbathInfo.OnReminderToggled(false))
            }
        }

        val localCheckedChange = remember {
            { checked: Boolean ->
                if (checked) {
                    if (hasPostNotificationsPermission(context)) {
                        eventSink(Event.SabbathInfo.OnReminderToggled(true))
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                } else {
                    eventSink(Event.SabbathInfo.OnReminderToggled(false))
                }
            }
        }

        ListItem(
            headlineContent = {
                Text(
                    text = "Enable Sabbath reminders",
                    color = colors.text
                )
            },
            modifier = modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(20.dp))
                .toggleable(
                    value = enabled,
                    role = Role.Switch,
                    onValueChange = localCheckedChange
                ),
            trailingContent = {
                Switch(
                    checked = enabled,
                    onCheckedChange = localCheckedChange
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            )
        )
    }
}

private fun hasPostNotificationsPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true
}

@PreviewLightDark
@Composable
private fun Preview() {
    val colors = rememberSabbathColors(isDark = isSystemInDarkTheme())
    HymnalTheme {
        Surface {
            ReminderInfoItem(
                enabled = true,
                eventSink = { },
            ).Content(colors)
        }
    }
}
