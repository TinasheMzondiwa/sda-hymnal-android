// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.navigation.number.NumberPadBottomSheet
import hymnal.services.content.HymnalContentProvider
import hymnal.services.prefs.education.Education
import hymnal.services.prefs.education.UserOrientation
import hymnal.sing.BottomBarOverlayState
import hymnal.sing.BottomBarState
import hymnal.sing.components.HymnContent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

@Stable
interface BottomBarStateProducer {
    @Composable
    operator fun invoke(hymn: HymnContent?, onIndex: (String) -> Unit): BottomBarState
}

@ContributesBinding(AppScope::class)
@Inject
class BottomBarStateProducerImpl(
    private val contentProvider: HymnalContentProvider,
    private val userOrientation: UserOrientation,
) : BottomBarStateProducer {
    @Composable
    override fun invoke(hymn: HymnContent?, onIndex: (String) -> Unit): BottomBarState {
        val coroutineScope = rememberStableCoroutineScope()
        var overlayState by rememberRetained { mutableStateOf<BottomBarOverlayState?>(null) }
        val showTuneTooltip by produceRetainedState(false) {
            userOrientation.shouldShow(Education.TunePlaybackTooltip)
                .catch { Timber.e(it) }
                .collect { value = it }
        }

        LaunchedEffect(showTuneTooltip) {
            if (showTuneTooltip) {
                coroutineScope.launch { userOrientation.track(Education.TunePlaybackTooltip) }
            }
        }

        return BottomBarState(
            number = hymn?.number ?: 1,
            isPlayEnabled = hymn != null,
            showTuneToolTip = showTuneTooltip,
            previousEnabled = hymn?.let { it.number > 1 } ?: false,
            nextEnabled = hymn?.let { it.number < 695 } ?: false,
            overlayState = overlayState,
            eventSink = { event ->
                when (event) {
                    BottomBarState.Event.OnGoToHymn -> {
                        overlayState = BottomBarOverlayState.NumberPadSheet(
                            onResult = { result ->
                                overlayState = null
                                when (result) {
                                    is NumberPadBottomSheet.Result.Cancel -> Unit
                                    is NumberPadBottomSheet.Result.Confirm -> {
                                        coroutineScope.launch {
                                            val selected = contentProvider.hymn(result.number) ?: return@launch
                                            onIndex(selected.index)
                                        }
                                    }
                                }
                            }
                        )
                    }
                    BottomBarState.Event.OnNextHymn -> {
                        val current = hymn?.number ?: return@BottomBarState
                        coroutineScope.launch {
                            val number = current + 1
                            val next = contentProvider.hymn(number) ?: return@launch
                            onIndex(next.index)
                        }
                    }
                    BottomBarState.Event.OnPreviousHymn -> {
                        val current = hymn?.number ?: return@BottomBarState
                        coroutineScope.launch {
                            val number = current - 1
                            val previous = contentProvider.hymn(number) ?: return@launch
                            onIndex(previous.index)
                        }
                    }
                }
            }
        )
    }
}