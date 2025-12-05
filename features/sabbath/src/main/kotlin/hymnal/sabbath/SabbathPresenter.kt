// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuitx.android.IntentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.navigation.SabbathScreen
import hymnal.sabbath.state.CurrentLocationStateProducer
import hymnal.sabbath.state.LocationResult
import hymnal.sabbath.state.SabbathInfoStateProducer
import hymnal.services.prefs.HymnalPrefs
import hymnal.services.prefs.model.ThemeStyle
import hymnal.services.sabbath.api.SabbathInfo
import hymnal.services.sabbath.api.SabbathRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

@AssistedInject
class SabbathPresenter(
    @Assisted private val navigator: Navigator,
    private val currentLocationStateProducer: CurrentLocationStateProducer,
    private val sabbathInfoStateProducer: SabbathInfoStateProducer,
    private val sabbathRepository: SabbathRepository,
    private val prefs: HymnalPrefs,
) : Presenter<State> {

    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        var locationResult by rememberRetained { mutableStateOf<LocationResult?>(null) }
        val themeStyle by produceRetainedState(ThemeStyle()) {
            prefs.themeStyle()
                .catch { Timber.e(it) }
                .collect { value = it }
        }

        val sabbathInfoState by rememberSabbathInfo(locationResult)
        val sabbathInfo = sabbathInfoState

        LaunchedEffect(Unit) {
            locationResult = currentLocationStateProducer()
        }

        return when (locationResult) {
            is LocationResult.NotGranted -> State.NoLocation(
                theme = themeStyle.theme,
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
                theme = themeStyle.theme,
                items = sabbathInfoStateProducer(navigator, sabbathInfo)
            )
            is LocationResult.NotAvailable -> State.LocationUnAvailable(
                theme = themeStyle.theme,
                eventSink = { event ->
                    when (event) {
                        Event.LocationUnAvailable.OnRetry -> {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            navigator.goTo(IntentScreen(intent))
                        }
                    }
                }
            )
            else -> State.Loading(theme = themeStyle.theme)
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
        fun create(navigator: Navigator): SabbathPresenter
    }
}