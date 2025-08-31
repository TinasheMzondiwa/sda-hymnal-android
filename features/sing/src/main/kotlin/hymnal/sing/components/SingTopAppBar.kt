// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.sing.TopBarState
import hymnal.sing.TopBarState.Event as TopBarEvent
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.libraries.l10n.R as L10nR
import hymnal.sing.R as SingR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SingTopAppBar(
    state: TopBarState,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val hapticFeedback = LocalAppHapticFeedback.current

    TopAppBar(
        title = { },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = {
                hapticFeedback.performClick()
                state.eventSink(TopBarEvent.OnNavBack)
            }) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    stringResource(L10nR.string.nav_back)
                )
            }
        },
        actions = {
            IconButton(onClick = {
                hapticFeedback.performClick()
                state.eventSink(TopBarEvent.OnStyleClick)
            }) {
                Icon(
                    Icons.Rounded.TextFormat,
                    stringResource(L10nR.string.text_format)
                )
            }
            IconButton(onClick = {
                hapticFeedback.performClick()
                state.eventSink(TopBarEvent.OnSaveClick)
            }) {
                Icon(
                    Icons.Rounded.BookmarkBorder,
                    stringResource(L10nR.string.save_to_collection)
                )
            }
            IconButton(onClick = {
                hapticFeedback.performClick()
                state.eventSink(TopBarEvent.OnFullscreenClick)
            }) {
                Icon(
                    painterResource(SingR.drawable.ic_mobile_landscape),
                    stringResource(L10nR.string.fullscreen)
                )
            }
            IconButton(onClick = {
                hapticFeedback.performClick()
                state.eventSink(TopBarEvent.OnShareClick)
            }) {
                Icon(
                    Icons.Rounded.Share,
                    stringResource(L10nR.string.share)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        ),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface { SingTopAppBar(state = TopBarState(overlayState = null) {}, Modifier.padding(16.dp)) }
    }
}