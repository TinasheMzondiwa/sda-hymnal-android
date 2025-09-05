// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.hymns

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import hymnal.libraries.navigation.CollectionHymnsScreen
import hymnal.services.content.repository.CollectionsRepository
import hymnal.services.model.HymnsCollection
import kotlinx.coroutines.flow.catch
import timber.log.Timber

@Inject
class CollectionHymnsPresenter(
    @Assisted private val screen: CollectionHymnsScreen,
    @Assisted private val navigator: Navigator,
    private val repository: CollectionsRepository,
) : Presenter<State> {
    @Composable
    override fun present(): State {
        val collection by produceRetainedState<HymnsCollection?>(null) {
            repository.getCollectionById(screen.collectionId)
                .catch { Timber.e(it) }
                .collect { value = it }
        }
        return State(
            title = collection?.title ?: "",
            description = collection?.description,
            color = collection?.color,
            eventSink = { event ->
                when (event) {
                    Event.OnNavBack -> navigator.pop()
                }
            }
        )
    }

    @AssistedFactory
    @CircuitInject(CollectionHymnsScreen::class, AppScope::class)
    interface Factory {
        fun create(screen: CollectionHymnsScreen, navigator: Navigator): CollectionHymnsPresenter
    }
}