// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import dev.zacsweers.metro.AppScope
import hymnal.donate.ui.TierButton
import hymnal.donate.ui.TierButtonSpec
import hymnal.libraries.navigation.DonateScreen
import hymnal.libraries.navigation.key.DonateSharedTransitionKey
import hymnal.ui.extensions.plus
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.size.HymnalDimens
import hymnal.ui.widget.scaffold.HazeScaffold
import kotlinx.collections.immutable.persistentListOf
import hymnal.libraries.l10n.R as L10nR

@OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@CircuitInject(DonateScreen::class, AppScope::class)
@Composable
fun DonateScreenUi(state: State, modifier: Modifier = Modifier) {
    val options = listOf("Once-off", "Subscriptions")
    var selectedOption by remember { mutableStateOf(options[0]) }

    SharedElementTransitionScope {
        HazeScaffold(
            modifier = modifier
                .sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            DonateSharedTransitionKey(
                                type = DonateSharedTransitionKey.ElementType.Button,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                ),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(L10nR.string.donate)) },
                    navigationIcon = {
                        IconButton(onClick = { state.eventSink(Event.OnClose) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = { BottomBar() },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item { PromptUi() }

                item {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        options.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = option == selectedOption,
                                onClick = { selectedOption = option },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContentColor = MaterialTheme.colorScheme.primary,
                                    inactiveContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                label = { Text(text = option) }
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }

                state.tiers.chunked(2).forEach { tiers ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            tiers.forEach { spec ->
                                TierButton(
                                    onClick = { state.eventSink(Event.SelectTier(spec)) },
                                    spec = spec,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun PromptUi(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Enjoying the app?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Your support helps us cover development costs, add new features, and keep the app running for everyone.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomBar(modifier: Modifier = Modifier) {
    val hapticFeedback = LocalAppHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val horizontalPadding = HymnalDimens.horizontalPadding()
    val contentPadding = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal
    ).asPaddingValues().plus(layoutDirection, start = horizontalPadding, end = horizontalPadding)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(contentPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = {
            hapticFeedback.performClick()
        }, modifier = Modifier.fillMaxWidth()) { Text("Donate $9.99") }
        Text(
            text = "Donations are voluntary and are processed securely through the App Store.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

internal val tiers = persistentListOf(
    TierButtonSpec(
        formattedPrice = "$0.99",
        label = null,
        selected = false
    ),
    TierButtonSpec(
        formattedPrice = "$4.99",
        label = null,
        selected = true
    ),
    TierButtonSpec(
        formattedPrice = "$9.99",
        label = null,
        selected = false
    ),
    TierButtonSpec(
        formattedPrice = "$14.99",
        label = null,
        selected = false
    ),
    TierButtonSpec(
        formattedPrice = "$19.99",
        label = null,
        selected = false
    ),
    TierButtonSpec(
        formattedPrice = "$24.99",
        label = null,
        selected = false,
    )
)

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    var selectedTier by remember { mutableStateOf<TierButtonSpec?>(null) }
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            DonateScreenUi(
                state = State(
                    tiers = tiers,
                    selectedTier = selectedTier,
                    eventSink = { event ->
                        when (event) {
                            Event.OnClose -> {}
                            is Event.SelectTier -> {
                                selectedTier = event.tier
                            }

                            Event.OnEnterCustomAmount -> Unit
                        }
                    }
                )
            )
        }
    }
}
