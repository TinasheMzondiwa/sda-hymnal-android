// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.immersive.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuitx.effects.ImpressionEffect
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.model.Hymnal
import hymnal.libraries.navigation.AppHomeScreen
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.libraries.navigation.number.NumberPadBottomSheet
import hymnal.services.content.HymnalContentProvider
import hymnal.services.model.Hymn
import hymnal.services.prefs.HymnalPrefs
import hymnal.sing.components.HymnContent
import hymnal.sing.immersive.TopBarOverlayState
import hymnal.sing.immersive.TopBarState
import hymnal.sing.state.TuneIndexStateProducer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import services.hymnal.firebase.AnalyticsService
import timber.log.Timber
import hymnal.sing.immersive.Event as UiEvent

@Stable
interface TopBarStateProducer {
    @Composable
    operator fun invoke(navigator: Navigator, hymn: Hymn?, onIndex: (String) -> Unit): TopBarState
}

@Inject
@ContributesBinding(scope = AppScope::class)
class TopBarStateProducerImpl(
    private val analyticsService: AnalyticsService,
    private val prefs: HymnalPrefs,
    private val contentProvider: HymnalContentProvider,
    private val tuneIndexStateProducer: TuneIndexStateProducer,
) : TopBarStateProducer {

    @Composable
    override fun invoke(
        navigator: Navigator,
        hymn: Hymn?,
        onIndex: (String) -> Unit
    ): TopBarState {
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

        val tune = tuneIndexStateProducer(hymn?.let { HymnContent(it) }, hymnal)
        var overlayState by rememberRetained { mutableStateOf<TopBarOverlayState?>(null) }

        ImpressionEffect { hymn?.let { logImpression(it.index, "IMMERSIVE") }}

        return TopBarState(
            number = hymn?.number ?: 1,
            tune = tune,
            isPlayEnabled = tune != null && hymn != null,
            overlayState = overlayState,
            eventSink = { event ->
                when (event) {
                    UiEvent.OnNavBack -> navigator.pop()
                    UiEvent.OnGoToHymn -> {
                        overlayState = TopBarOverlayState.NumberPadSheet(
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

                                            logImpression(
                                                index = selected.index,
                                                source = SingHymnScreen.Source.NUMBER_PICKER.name,
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

            }
        )
    }

    private fun logImpression(index: String, source: String) {
        analyticsService.logEvent(
            eventName = "open_hymn",
            params = mapOf("hymn_index" to index, "source" to source)
        )
    }

}