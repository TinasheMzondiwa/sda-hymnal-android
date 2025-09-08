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
import dev.zacsweers.metro.Inject
import hymnal.libraries.navigation.SabbathScreen
import hymnal.sabbath.state.CountdownStateProducer
import hymnal.sabbath.state.CurrentLocationStateProducer
import hymnal.sabbath.state.LocationResult
import hymnal.services.sabbath.api.SabbathInfo
import hymnal.services.sabbath.api.SabbathRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Inject
class SabbathPresenter(
    private val countdownStateProducer: CountdownStateProducer,
    private val currentLocationStateProducer: CurrentLocationStateProducer,
    private val sabbathRepository: SabbathRepository
) : Presenter<State> {

    private val formatter = DateTimeFormatter.ofPattern("EE, h:mm a")

    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        var locationResult by rememberRetained { mutableStateOf<LocationResult?>(null) }

        val sabbathInfoState by rememberSabbathInfo(locationResult)
        val sabbathInfo = sabbathInfoState

        LaunchedEffect(Unit) {
            locationResult = currentLocationStateProducer()
        }

        val countDownTime by rememberCountDownTime(sabbathInfo)
        val sabbathProgress by rememberSabbathProgress(sabbathInfo)

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
                location = sabbathInfo.location,
                isSabbath = sabbathInfo.isSabbath,
                progress = sabbathProgress,
                countDown = countDownTime,
                sabbathStart = sabbathInfo.sabbathStart.format(formatter),
                sabbathEnd = sabbathInfo.sabbathEnd.format(formatter),
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

    @Composable
    private fun rememberCountDownTime(sabbathInfo: SabbathInfo?) =
        produceRetainedState("", sabbathInfo) {
            sabbathInfo?.run {
                val flow = if (isSabbath) {
                    countdownStateProducer(sabbathEnd, true)
                } else {
                    countdownStateProducer(sabbathStart, false)
                }
                flow.catch { Timber.e(it) }
                    .collect { value = it }
            }?.run {
                value = "---"
            }
        }

    @Composable
    private fun rememberSabbathProgress(sabbathInfo: SabbathInfo?) =
        produceRetainedState(0f, sabbathInfo) {
            sabbathInfo?.run {
                val progress = when {
                    isSabbath -> {
                        val now = ZonedDateTime.now()
                        val total = Duration.between(sabbathStart, sabbathEnd).toMillis().coerceAtLeast(1)
                        val part = Duration.between(sabbathStart, now).toMillis().coerceAtLeast(0)
                        (part.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                    }
                    else -> 0f
                }
                value = progress
            }?.run {
                value = 0f
            }
        }

    @CircuitInject(SabbathScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(): SabbathPresenter
    }
}