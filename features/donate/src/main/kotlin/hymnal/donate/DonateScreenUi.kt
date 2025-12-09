// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.zacsweers.metro.AppScope
import hymnal.donate.ui.DonateTopBar
import hymnal.donate.ui.TierButton
import hymnal.libraries.navigation.DonateScreen
import hymnal.libraries.navigation.key.DonateSharedTransitionKey
import hymnal.ui.extensions.plus
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.size.HymnalDimens
import hymnal.ui.widget.placeholder.placeholder
import hymnal.ui.widget.scaffold.HazeScaffold
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalHazeMaterialsApi::class
)
@CircuitInject(DonateScreen::class, AppScope::class)
@Composable
fun DonateScreenUi(state: State, modifier: Modifier = Modifier) {
    SharedElementTransitionScope {
        val showButtons by remember { derivedStateOf { !isTransitionActive } }
        val scrollBehavior =
            TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        val horizontalPadding = HymnalDimens.horizontalPadding(24.dp)
        val layoutDirection = LocalLayoutDirection.current

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
                )
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { DonateTopBar(state = state, scrollBehavior = scrollBehavior) },
            bottomBar = { BottomBar(state) },
            hazeStyle = HazeMaterials.ultraThin(MaterialTheme.colorScheme.secondaryContainer),
            blurTopBar = true,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding.plus(
                    layoutDirection = layoutDirection,
                    start = horizontalPadding,
                    top = 16.dp,
                    end = horizontalPadding,
                    bottom = 0.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item { PromptUi(Modifier.skipToLookaheadSize()) }

                item { Spacer(Modifier.height(8.dp)) }

                if (showButtons && state is State.Donate) {
                    item("tabs") { TabsUi(state, Modifier.skipToLookaheadSize()) }

                    item("buttons") {
                        TierButtons(
                            state = state,
                            modifier = Modifier
                                .skipToLookaheadSize()
                                .animateItem()
                        )
                    }
                } else {
                    item("loading") {
                        FlowRow(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            maxItemsInEachRow = 2,
                        ) {
                            repeat(6) {
                                Spacer(
                                    Modifier
                                        .weight(1f)
                                        .height(32.dp)
                                        .placeholder(
                                            visible = true,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.3f
                                            ),
                                            shape = RoundedCornerShape(20.dp),
                                            highlightColor = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.1f
                                            )
                                        )
                                )
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }

                item("disclaimer") {
                    Text(
                        text = stringResource(L10nR.string.in_app_purchases_disclaimer),
                        modifier = Modifier.skipToLookaheadSize().animateItem(),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)) }
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
                    shape = CircleShape,
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
            text = stringResource(L10nR.string.donate_prompt_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(L10nR.string.donate_prompt_desc),
            modifier = Modifier,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TabsUi(state: State.Donate, modifier: Modifier = Modifier) {
    val hapticFeedback = LocalAppHapticFeedback.current

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        DonateType.entries.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == state.type,
                onClick = {
                    hapticFeedback.performSegmentSwitch()
                    state.eventSink(Event.OnSelectDonateType(option))
                },
                colors = SegmentedButtonDefaults.colors(
                    activeContentColor = MaterialTheme.colorScheme.primary,
                    inactiveContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = DonateType.entries.size
                ),
                label = { Text(text = stringResource(option.titleRes)) }
            )
        }
    }
}

@Composable
private fun TierButtons(state: State.Donate, modifier: Modifier = Modifier) {
    val hapticFeedback = LocalAppHapticFeedback.current

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        maxItemsInEachRow = 2,
    ) {
        state.tiers.forEach { spec ->
            TierButton(
                onClick = {
                    hapticFeedback.performToggleSwitch(true)
                    state.eventSink(Event.SelectTier(spec))
                },
                spec = spec,
                selected = spec == state.selectedTier,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomBar(state: State, modifier: Modifier = Modifier) {
    val hapticFeedback = LocalAppHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val horizontalPadding = HymnalDimens.horizontalPadding()
    val contentPadding = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal
    ).asPaddingValues().plus(layoutDirection, start = horizontalPadding, end = horizontalPadding)
    val resources = LocalResources.current
    val activity = LocalActivity.current

    val (enabled, label) = when (state) {
        is State.Donate -> {
            state.selectedTier?.let { tier ->
                true to when (state.type) {
                    DonateType.ONE_TIME -> resources.getString(
                        L10nR.string.donation_one_time_action,
                        tier.formattedPrice
                    )

                    DonateType.SUBSCRIPTION -> resources.getString(
                        L10nR.string.donation_monthly_action,
                        tier.formattedPrice
                    )
                }
            } ?: (false to stringResource(L10nR.string.donate))
        }

        is State.Loading -> false to stringResource(L10nR.string.donate)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(contentPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                hapticFeedback.performClick()
                activity?.let {
                    (state as? State.Donate)?.eventSink(Event.OnPrimaryButtonClick(it))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
        ) { Text(text = label) }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            DonateScreenUi(
                state = State.Loading {}
            )
        }
    }
}
