// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.DonateScreen
import hymnal.libraries.navigation.key.DonateSharedTransitionKey
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.scaffold.HazeScaffold
import kotlinx.collections.immutable.persistentListOf
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@CircuitInject(DonateScreen::class, AppScope::class)
@Composable
fun DonateScreenUi(state: State, modifier: Modifier = Modifier) {
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
                        IconButton(onClick = { state.eventSink(State.Event.OnClose) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

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

                    Spacer(modifier = Modifier.height(8.dp))

                    state.tiers.forEach { tier ->
                        DonationButton(
                            tier = tier,
                            isSelected = state.selectedTier == tier,
                            onClick = { state.eventSink(State.Event.SelectTier(tier)) }
                        )
                    }

                    TextButton(onClick = { state.eventSink(State.Event.OnEnterCustomAmount) }) {
                        Text(text = "Enter a custom amount")
                    }
                }

                Text(
                    text = "Donations are voluntary and are processed securely through the App Store.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DonationButton(
    tier: DonationTier,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = tier.formattedPrice,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center)
            )
            if (tier.isMostPopular) {
                Badge(
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Text(text = "Most Popular", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                }
            }
        }
    }
}

internal val tiers = persistentListOf(
    DonationTier(
        id = "1",
        formattedPrice = "$0.99",
        isMostPopular = false
    ),
    DonationTier(
        id = "5",
        formattedPrice = "$4.99",
        isMostPopular = true
    ),
    DonationTier(
        id = "10",
        formattedPrice = "$9.99",
        isMostPopular = false
    ),
    DonationTier(
        id = "15",
        formattedPrice = "$14.99",
        isMostPopular = false
    ),
    DonationTier(
        id = "20",
        formattedPrice = "$19.99",
        isMostPopular = false
    ),
    DonationTier(
        id = "25",
        formattedPrice = "$24.99",
        isMostPopular = false,
    )
)

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    var selectedTier by remember { mutableStateOf<DonationTier?>(null) }
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            DonateScreenUi(
                state = State(
                    tiers = tiers,
                    selectedTier = selectedTier,
                    eventSink = { event ->
                        when (event) {
                            State.Event.OnClose -> {}
                            is State.Event.SelectTier -> {
                                selectedTier = event.tier
                            }
                            State.Event.OnEnterCustomAmount -> Unit
                        }
                    }
                )
            )
        }
    }
}
