// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.sabbath

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.navigation.SabbathHymnsScreen
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.services.content.HymnalContentProvider
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.catch
import timber.log.Timber

@AssistedInject
class SabbathHymnsPresenter(
    @Assisted private val navigator: Navigator,
    private val contentProvider: HymnalContentProvider,
) : Presenter<State> {
    @Composable
    override fun present(): State {
        val hymns by produceRetainedState(emptyList()) {
            contentProvider.sabbathHymns()
                .catch { Timber.e(it) }
                .collect { value = it }
        }

        return State(
            hymns = hymns.toImmutableList(),
            eventSink = { event ->
                when (event) {
                    Event.OnNavBackClicked -> navigator.pop()
                    is Event.OnHymnClicked -> navigator.goTo(
                        SingHymnScreen(
                            index = event.index,
                            source = SingHymnScreen.Source.SABBATH,
                        )
                    )
                }
            }
        )
    }

    @CircuitInject(SabbathHymnsScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): SabbathHymnsPresenter
    }
}