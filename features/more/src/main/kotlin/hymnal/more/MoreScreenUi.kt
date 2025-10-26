// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitContent
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.AccountCardScreen
import hymnal.more.components.AboutCard
import hymnal.more.components.InfoItemsCard
import hymnal.more.components.MoreTopAppBar
import hymnal.libraries.navigation.MoreScreen
import hymnal.ui.extensions.plus
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.size.HymnalDimens
import hymnal.ui.widget.scaffold.HazeScaffold
import kotlinx.collections.immutable.persistentListOf
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(MoreScreen::class, AppScope::class)
@Composable
fun MoreScreenUi(state: State, modifier: Modifier = Modifier) {
    val scrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val layoutDirection = LocalLayoutDirection.current
    val context = LocalContext.current
    val hapticFeedback = LocalAppHapticFeedback.current
    val horizontalPadding = HymnalDimens.horizontalPadding()

    HazeScaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MoreTopAppBar(
                scrollBehavior = scrollBehavior,
                onDonateClick = {
                    hapticFeedback.performSuccess()
                    state.eventSink(Event.OnDonateClick)
                },
            )
        },
        blurTopBar = true,
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding.plus(
                layoutDirection = layoutDirection,
                start = horizontalPadding,
                end = horizontalPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item("version-info") { AboutCard(version = state.appVersion) }

            item(key = "account-card") {
                CircuitContent(
                    screen = AccountCardScreen,
                    onNavEvent = {
                        state.eventSink(Event.OnNavEvent(it))
                    }
                )
            }

            itemCards {
                hapticFeedback.performClick()
                state.eventSink(Event.OnLinkClick(it, context))
            }

            item("app-info") {
                Text(
                    text = stringResource(L10nR.string.app_info),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun LazyListScope.itemCards(onLinkClick: (InfoLink) -> Unit) {
    item("support-info") {
        InfoItemsCard(
            section = stringResource(L10nR.string.support),
            items = persistentListOf(InfoLink.Feedback, InfoLink.PrivacyPolicy),
            onClick = onLinkClick,
        )
    }

    item("community-info") {
        InfoItemsCard(
            section = stringResource(L10nR.string.community),
            items = persistentListOf(InfoLink.ShareApp, InfoLink.Rate, InfoLink.Review),
            onClick = onLinkClick,
        )
    }
}
