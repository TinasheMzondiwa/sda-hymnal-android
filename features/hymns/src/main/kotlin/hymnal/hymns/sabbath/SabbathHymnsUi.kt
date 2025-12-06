// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.sabbath

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.hymns.SortType
import hymnal.hymns.components.HymnCard
import hymnal.libraries.navigation.SabbathHymnsScreen
import hymnal.ui.extensions.plus
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.size.HymnalDimens
import hymnal.ui.widget.scaffold.HazeScaffold
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(SabbathHymnsScreen::class, AppScope::class)
@Composable
fun SabbathHymnsUi(state: State, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    val hapticFeedback = LocalAppHapticFeedback.current
    val listState: LazyListState = rememberLazyListState()
    val horizontalPadding = HymnalDimens.horizontalPadding()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    HazeScaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(L10nR.string.sabbath_playlist)) },
                navigationIcon = {
                    IconButton(onClick = {
                        hapticFeedback.performClick()
                        state.eventSink(Event.OnNavBackClicked)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                )
            )
        },
        blurTopBar = true,
    ) { contentPadding ->
        LazyColumn(
            state = listState,
            contentPadding = contentPadding.plus(
                layoutDirection = layoutDirection,
                top = 12.dp,
                start = horizontalPadding,
                end = horizontalPadding,
            ),
        ) {
            items(state.hymns, key = { it.index }) { hymn ->
                HymnCard(
                    hymn = hymn,
                    sortType = SortType.NUMBER,
                    modifier = Modifier.animateItem(),
                    onClick = {
                        hapticFeedback.performClick()
                        state.eventSink(Event.OnHymnClicked(hymn.index))
                    },
                )
            }
        }
    }
}