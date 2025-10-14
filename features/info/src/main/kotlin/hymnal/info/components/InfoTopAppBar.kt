// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.info.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import hymnal.libraries.navigation.key.DonateSharedTransitionKey
import hymnal.info.R as InfoR
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun InfoTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onDonateClick: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = stringResource(L10nR.string.info)) },
        modifier = modifier,
        actions = {
            SharedElementTransitionScope {
                FilledTonalButton(
                    onClick = onDonateClick,
                    modifier = Modifier
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
                    elevation = ButtonDefaults.filledTonalButtonElevation(defaultElevation = 4.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            painter = painterResource(InfoR.drawable.ic_heart_smile),
                            contentDescription = null
                        )
                        Text(stringResource(L10nR.string.donate))
                    }
                }
            }

            Spacer(Modifier.size(12.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        ),
        scrollBehavior = scrollBehavior,
    )
}