// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.services.content.HymnalContentProvider
import hymnal.services.prefs.HymnalPrefs
import hymnal.sing.components.HymnContent
import hymnal.sing.components.model.TextStyleSpec
import hymnal.sing.state.BottomBarStateProducer
import hymnal.sing.state.TopBarStateProducer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

@AssistedInject
class SingHymnPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: SingHymnScreen,
    private val contentProvider: HymnalContentProvider,
    private val prefs: HymnalPrefs,
    private val topBarStateProducer: TopBarStateProducer,
    private val bottomBarStateProducer: BottomBarStateProducer,
) : Presenter<State> {

    @Composable
    override fun present(): State {
        var hymnIndex by rememberRetained { mutableStateOf(screen.index) }
        var overlayState by rememberRetained { mutableStateOf<SingOverlayState?>(null) }
        val hymnContent by produceRetainedState<HymnContent?>(null, hymnIndex) {
            contentProvider.hymn(hymnIndex)
                .catch { Timber.e(it) }
                .collect { value = it?.let { HymnContent(it) } }
        }
        val textStyle by produceRetainedState(TextStyleSpec()) {
            prefs.themeStyle()
                .map { TextStyleSpec(font = it.font, textSize = it.textSize) }
                .catch { Timber.e(it) }
                .collect { value = it }
        }
        val hymn = hymnContent

        val topBarState = topBarStateProducer(navigator = navigator, hymn = hymn)
        val bottomBarState = bottomBarStateProducer(hymn) { hymnIndex = it }

        return when {
            hymn != null -> State.Content(
                hymn = hymn,
                topBarState = topBarState,
                bottomBarState = bottomBarState,
                textStyle = textStyle,
                overlayState = overlayState ?: topBarState.overlayState,
                eventSink = { event -> }
            )
            else -> State.Loading(screen.index)
        }
    }

    @CircuitInject(SingHymnScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator, screen: SingHymnScreen): SingHymnPresenter
    }
}