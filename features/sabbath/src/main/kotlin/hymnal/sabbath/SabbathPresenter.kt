// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.navigation.SabbathScreen
import hymnal.sabbath.state.CurrentLocationStateProducer
import hymnal.sabbath.state.LocationResult
import hymnal.sabbath.state.SabbathInfoStateProducer
import hymnal.services.sabbath.api.SabbathInfo
import hymnal.services.sabbath.api.SabbathRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

@AssistedInject
class SabbathPresenter(
    private val currentLocationStateProducer: CurrentLocationStateProducer,
    private val sabbathInfoStateProducer: SabbathInfoStateProducer,
    private val sabbathRepository: SabbathRepository
) : Presenter<State> {

    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        var locationResult by rememberRetained { mutableStateOf<LocationResult?>(null) }

        val sabbathInfoState by rememberSabbathInfo(locationResult)
        val sabbathInfo = sabbathInfoState

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
                })

            is LocationResult.Granted if sabbathInfo != null -> State.SabbathInfo(
                items = sabbathInfoStateProducer(sabbathInfo)
            )
            else -> State.Loading
        }
    }

    @Composable
    private fun rememberSabbathInfo(locationResult: LocationResult?) =
        produceRetainedState<SabbathInfo?>(null, locationResult) {
            val location =
                (locationResult as? LocationResult.Granted)?.location ?: return@produceRetainedState
            sabbathRepository.getSabbathInfo(
                latitude = location.latitude,
                longitude = location.longitude,
            ).catch { Timber.e(it) }
                .collect { value = it.getOrNull() }
        }

    @CircuitInject(SabbathScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(): SabbathPresenter
    }
}