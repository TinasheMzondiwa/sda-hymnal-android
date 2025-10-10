// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.collections.create.CreateCollectionScreen
import hymnal.libraries.navigation.AddToCollectionScreen
import hymnal.services.content.repository.CollectionsRepository
import hymnal.services.model.HymnsCollection
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

@AssistedInject
class AddToCollectionPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: AddToCollectionScreen,
    private val repository: CollectionsRepository,
) : Presenter<State> {

    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        val collectionsState by produceRetainedState<List<HymnsCollection>?>(null) {
            repository.listAll()
                .catch { Timber.e(it) }
                .collect { value = it }
        }

        val collections = collectionsState

        val eventSink: (Event) -> Unit = { event: Event ->
            when (event) {
                Event.CreateNewCollection -> {
                    navigator.goTo(CreateCollectionScreen(showUpNavigation = true))
                }
                is Event.CollectionSelected -> {
                    coroutineScope.launch {
                        val collectionId = event.spec.id
                        val hymnId = screen.hymnId

                        if (event.spec.isSelected) {
                            repository.removeHymnFromCollection(collectionId, hymnId)
                        } else {
                            repository.addHymnToCollection(collectionId, hymnId)
                        }
                    }
                }
            }
        }

        return when {
            collections == null -> State.Loading(eventSink)
            collections.isEmpty() -> State.Empty(eventSink)
            else -> State.Choose(
                collections = collections.map { collection->
                    ChooseCollectionSpec(
                        collection = collection,
                        isSelected = collection.hymns.associateBy { it.index }.contains(screen.hymnId)
                    )
                }.toImmutableList(),
                eventSink = eventSink,
            )
        }
    }

    @CircuitInject(AddToCollectionScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator, screen: AddToCollectionScreen): AddToCollectionPresenter
    }
}