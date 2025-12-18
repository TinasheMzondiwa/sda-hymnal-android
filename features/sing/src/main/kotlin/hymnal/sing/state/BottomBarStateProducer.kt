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
import hymnal.libraries.model.Hymnal
import hymnal.libraries.navigation.number.NumberPadBottomSheet
import hymnal.services.content.HymnalContentProvider
import hymnal.services.prefs.HymnalPrefs
import hymnal.services.prefs.education.Education
import hymnal.services.prefs.education.UserOrientation
import hymnal.sing.BottomBarOverlayState
import hymnal.sing.BottomBarState
import hymnal.sing.components.HymnContent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import hymnal.libraries.l10n.R as L10nR

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
    private val prefs: HymnalPrefs,
    private val tuneIndexStateProducer: TuneIndexStateProducer,
) : BottomBarStateProducer {
    @Composable
    override fun invoke(hymn: HymnContent?, onIndex: (String) -> Unit): BottomBarState {
        val coroutineScope = rememberStableCoroutineScope()
        val hymnal by produceRetainedState(Hymnal.NewHymnal, hymn) {
            prefs.currentHymnal()
                .map {
                    // Use the correct hymn hymnal
                    if (hymn != null && it.year != hymn.year) {
                        Hymnal.fromYear(hymn.year)
                    } else {
                        it
                    }
                }
                .filterNotNull()
                .catch { Timber.e(it) }
                .collect { value = it }
        }
        val tuneIndex = tuneIndexStateProducer(hymn?.index ?: "", hymnal)
        var overlayState by rememberRetained { mutableStateOf<BottomBarOverlayState?>(null) }
        val showTuneTooltip by produceRetainedState(false, hymnal) {
            if (!tuneIndex.isNullOrEmpty()) {
                userOrientation.shouldShow(Education.TunePlaybackTooltip)
                    .catch { Timber.e(it) }
                    .collect { value = it }
            }
        }

        LaunchedEffect(showTuneTooltip) {
            if (showTuneTooltip) {
                coroutineScope.launch { userOrientation.track(Education.TunePlaybackTooltip) }
            }
        }

        return BottomBarState(
            number = hymn?.number ?: 1,
            tuneIndex = tuneIndex,
            isPlayEnabled = tuneIndex != null && hymn != null,
            showTuneToolTip = showTuneTooltip,
            previousEnabled = hymn?.let { it.number > 1 } ?: false,
            nextEnabled = hymn?.let { it.number < hymnal.hymns } ?: false,
            titleLabelRes = if (hymnal == Hymnal.Choruses) {
                L10nR.string.chorus_number
            } else {
                L10nR.string.hymn_number
            },
            overlayState = overlayState,
            eventSink = { event ->
                when (event) {
                    BottomBarState.Event.OnGoToHymn -> {
                        overlayState = BottomBarOverlayState.NumberPadSheet(
                            hymns = hymnal.hymns,
                            onResult = { result ->
                                overlayState = null
                                when (result) {
                                    is NumberPadBottomSheet.Result.Cancel -> Unit
                                    is NumberPadBottomSheet.Result.Confirm -> {
                                        coroutineScope.launch {
                                            val selected = contentProvider.hymn(
                                                number = result.number,
                                                year = hymnal.year
                                            ) ?: return@launch
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
                            val next = contentProvider.hymn(number = number, year = hymnal.year)
                                ?: return@launch
                            onIndex(next.index)
                        }
                    }
                    BottomBarState.Event.OnPreviousHymn -> {
                        val current = hymn?.number ?: return@BottomBarState
                        coroutineScope.launch {
                            val number = current - 1
                            val previous = contentProvider.hymn(number = number, year = hymnal.year)
                                ?: return@launch
                            onIndex(previous.index)
                        }
                    }
                }
            }
        )
    }
}