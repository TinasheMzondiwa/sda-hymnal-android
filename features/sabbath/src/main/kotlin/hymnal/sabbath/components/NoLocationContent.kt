// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.LocationSearching
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import hymnal.sabbath.Event
import hymnal.sabbath.State
import hymnal.services.prefs.model.AppTheme
import hymnal.ui.theme.HymnalTheme
import hymnal.libraries.l10n.R as L10nR

@Composable
internal fun NoLocationContent(state: State.NoLocation, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            state.eventSink(Event.NoLocation.OnLocationGranted)
        } else {
            state.eventSink(Event.NoLocation.OnLocationDenied)
        }
    }

    NoLocationContent(
        title = stringResource(L10nR.string.sabbath_enable_location),
        description = stringResource(L10nR.string.sabbath_permission_location_prompt),
        onClick = {
            if (hasLocationPermissions(context)) {
                state.eventSink(Event.NoLocation.OnLocationGranted)
            } else {
                permissionLauncher.launch(locationPermissions)
            }
        },
        modifier = modifier
    ) {
        Icon(imageVector = Icons.Rounded.LocationSearching, contentDescription = null)
        Spacer(Modifier.size(12.dp))
        Text(text = stringResource(L10nR.string.sabbath_grant_permission))
    }
}

private fun hasLocationPermissions(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

@Composable
internal fun NoLocationContent(state: State.LocationUnAvailable, modifier: Modifier = Modifier) {
    NoLocationContent(
        title = stringResource(L10nR.string.sabbath_location_unavailable),
        description = stringResource(L10nR.string.sabbath_location_unavailable_prompt),
        onClick = { state.eventSink(Event.LocationUnAvailable.OnRetry) },
        modifier = modifier
    ) {
        Icon(imageVector = Icons.Rounded.Settings, contentDescription = null)
        Spacer(Modifier.size(12.dp))
        Text(text = stringResource(L10nR.string.sabbath_location_settings))
    }
}

@Composable
private fun NoLocationContent(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonContent: @Composable RowScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOff,
                contentDescription = null,
                modifier = Modifier,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.size(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.size(16.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.size(24.dp))

        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            content = buttonContent,
        )
    }
}


@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            NoLocationContent(
                state = State.NoLocation(
                    theme = AppTheme.FOLLOW_SYSTEM,
                    eventSink = {},
                )
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewNotAvailable() {
    HymnalTheme {
        Surface {
            NoLocationContent(
                state = State.LocationUnAvailable(
                    theme = AppTheme.FOLLOW_SYSTEM,
                    eventSink = {},
                )
            )
        }
    }
}