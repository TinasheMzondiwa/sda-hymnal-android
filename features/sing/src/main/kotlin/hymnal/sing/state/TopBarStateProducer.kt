/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.sing.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.sing.SingOverlayState
import hymnal.sing.TopBarState
import hymnal.sing.components.text.TextStyleScreen

@Stable
interface TopBarStateProducer {
    @Composable
    operator fun invoke(navigator: Navigator): TopBarState
}

@Inject
@ContributesBinding(scope = AppScope::class)
class TopBarStateProducerImpl : TopBarStateProducer {
    @Composable
    override fun invoke(navigator: Navigator): TopBarState {
        var overlayState by rememberRetained { mutableStateOf<SingOverlayState?>(null) }

        return TopBarState(
            overlayState = overlayState,
            eventSink = { event ->
                when (event) {
                    is TopBarState.Event.OnNavBack -> navigator.pop()
                    TopBarState.Event.OnFullscreenClick -> Unit
                    TopBarState.Event.OnSaveClick -> Unit
                    TopBarState.Event.OnShareClick -> Unit
                    TopBarState.Event.OnStyleClick -> {
                        overlayState =
                            SingOverlayState.BottomSheet(
                                screen = TextStyleScreen,
                                skipPartiallyExpanded = true,
                            ) { overlayState = null }
                    }
                }
            }
        )
    }
}