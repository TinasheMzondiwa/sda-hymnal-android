// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.collections.CollectionOverlayState.BottomSheet
import hymnal.collections.create.CreateCollectionScreen
import hymnal.libraries.navigation.CollectionHymnsScreen
import hymnal.libraries.navigation.CollectionsScreen
import hymnal.services.content.repository.CollectionsRepository
import hymnal.services.model.HymnsCollection
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

@AssistedInject
class CollectionsPresenter(
    @Assisted private val navigator: Navigator,
    private val repository: CollectionsRepository,
) : Presenter<State> {
    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        var overlayState by rememberRetained { mutableStateOf<CollectionOverlayState?>(null) }
        val collectionsState by produceRetainedState<List<HymnsCollection>?>(null) {
            repository.listAll()
                .catch { Timber.e(it) }
                .collect { value = it }
        }

        val collections = collectionsState

        val eventSink: (Event) -> Unit = { event: Event ->
            when (event) {
                Event.OnAddCollectionClicked -> {
                    overlayState =
                        BottomSheet(
                            screen = CreateCollectionScreen(),
                            skipPartiallyExpanded = true,
                            onResult = { result ->
                                overlayState = null
                            },
                        )
                }
                is Event.OnCollectionClicked -> {
                    navigator.goTo(CollectionHymnsScreen(event.collection.collectionId))
                }

                is Event.OnDeleteCollectionClicked -> coroutineScope.launch {
                    repository.deleteCollection(event.collection.collectionId)
                }
            }
        }

        return when {
            collections == null -> State.Loading(eventSink = eventSink)
            collections.isEmpty() -> State.Empty(overlayState, eventSink)
            else -> State.Content(
                collections = collections.toImmutableList(),
                overlayState = overlayState,
                eventSink = eventSink,
            )
        }
    }

    @CircuitInject(CollectionsScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): CollectionsPresenter
    }
}