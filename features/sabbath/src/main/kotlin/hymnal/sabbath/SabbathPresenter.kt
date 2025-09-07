// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import hymnal.libraries.navigation.SabbathScreen
import hymnal.sabbath.state.CurrentLocationStateProducer
import hymnal.sabbath.state.LocationResult
import kotlinx.coroutines.launch

@Inject
class SabbathPresenter(
    private val currentLocationStateProducer: CurrentLocationStateProducer
) : Presenter<State> {

    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        var locationResult by rememberRetained { mutableStateOf<LocationResult?>(null) }

        LaunchedEffect(Unit) {
            locationResult = currentLocationStateProducer()
        }

        return when (locationResult) {
            null -> State.Loading
            is LocationResult.NotGranted -> State.NoLocation(
                eventSink = { event ->
                    when (event) {
                        Event.NoLocation.OnLocationDenied -> Unit
                        is Event.NoLocation.OnLocationGranted -> {
                            coroutineScope.launch {
                                locationResult = currentLocationStateProducer()
                            }
                        }
                    }
                }
            )

            is LocationResult.Granted -> State.SabbathInfo(
                location = "Canada"
            )
        }
    }

    @CircuitInject(SabbathScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(): SabbathPresenter
    }
}