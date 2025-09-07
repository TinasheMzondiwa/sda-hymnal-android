// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.hymns

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
import dev.zacsweers.metro.Inject
import hymnal.libraries.navigation.CollectionHymnsScreen
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.services.content.repository.CollectionsRepository
import hymnal.services.model.HymnsCollection
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

@Inject
class CollectionHymnsPresenter(
    @Assisted private val screen: CollectionHymnsScreen,
    @Assisted private val navigator: Navigator,
    private val repository: CollectionsRepository,
) : Presenter<State> {
    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        val collectionState by produceRetainedState<HymnsCollection?>(null) {
            repository.getCollectionById(screen.collectionId)
                .catch { Timber.e(it) }
                .collect { value = it }
        }
        val collection = collectionState

        return when {
            collection == null -> State.Loading(id = screen.collectionId)
            else ->
                State.Content(
                    id = collection.collectionId,
                    title = collection.title,
                    description = collection.description?.takeUnless { it.isEmpty() },
                    color = collection.color,
                    hymns = collection.hymns.toImmutableList(),
                    eventSink = { event ->
                        when (event) {
                            is Event.OnNavBack -> navigator.pop()
                            is Event.OnHymnClicked -> navigator.goTo(SingHymnScreen(event.index))
                            Event.OnDeleteCollectionClicked -> {
                                coroutineScope.launch {
                                    repository.deleteCollection(collection.collectionId)
                                    navigator.pop()
                                }
                            }
                        }
                    },
                )
        }
    }

    @AssistedFactory
    @CircuitInject(CollectionHymnsScreen::class, AppScope::class)
    interface Factory {
        fun create(screen: CollectionHymnsScreen, navigator: Navigator): CollectionHymnsPresenter
    }
}