// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.services.content.HymnalContentProvider
import hymnal.services.prefs.HymnalPrefs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

@Stable
interface LastOpenedHymnStateProducer {
    @Composable
    operator fun invoke(navigator: Navigator): OpenedHymnState
}

sealed interface OpenedHymnState : CircuitUiState {
    data class Visible(
        val index: String,
        val number: Int,
        val title: String,
        val eventSink: (OpenedHymnEvent) -> Unit,
    ) : OpenedHymnState

    data object Hidden : OpenedHymnState
}

sealed interface OpenedHymnEvent : CircuitUiEvent {
    data class GoToHymn(val index: String): OpenedHymnEvent
    data object DismissCard : OpenedHymnEvent
}

@ContributesBinding(AppScope::class)
@Inject
class LastOpenedHymnStateProducerImpl(
    private val prefs: HymnalPrefs,
    private val contentProvider: HymnalContentProvider,
) : LastOpenedHymnStateProducer {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    override fun invoke(navigator: Navigator): OpenedHymnState {
        val coroutineScope = rememberStableCoroutineScope()

        val lastHymn by produceRetainedState<OpenedHymn?>(null) {
            combine(
                prefs.showLastHymn(),
                prefs.lastHymnIndex(),
            ) { defaultToLastHymn, lastHymnIndex ->
                defaultToLastHymn to lastHymnIndex
            }.flatMapLatest { (defaultToLastHymn, lastHymnIndex) ->
                if (defaultToLastHymn && lastHymnIndex != null) {
                    contentProvider.hymn(lastHymnIndex)
                        .filterNotNull()
                        .map { hymn ->
                            OpenedHymn(
                                index = hymn.index,
                                number = hymn.number,
                                title = hymn.title,
                            )
                        }
                } else {
                    flowOf(null)
                }
            }
                .catch { Timber.e(it) }
                .collect { value = it }
        }

        return when (val hymn = lastHymn) {
            null -> OpenedHymnState.Hidden
            else -> OpenedHymnState.Visible(
                index = hymn.index,
                number = hymn.number,
                title = hymn.title,
                eventSink = { event ->
                    when (event) {
                        OpenedHymnEvent.DismissCard -> {
                            coroutineScope.launch { prefs.setShowLastHymn(false) }
                        }
                        is OpenedHymnEvent.GoToHymn -> {
                            navigator.goTo(SingHymnScreen(event.index))
                        }
                    }
                },
            )
        }
    }

}

private data class OpenedHymn(
    val index: String,
    val number: Int,
    val title: String,
)