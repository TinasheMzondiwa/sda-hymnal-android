// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.DonateScreen
import hymnal.libraries.navigation.key.DonateSharedTransitionKey
import hymnal.ui.widget.scaffold.HazeScaffold

@OptIn(ExperimentalSharedTransitionApi::class)
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
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {

        }
    }
}