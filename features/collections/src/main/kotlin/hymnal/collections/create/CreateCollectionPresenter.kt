// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import hymnal.collections.components.CollectionColor
import hymnal.services.content.repository.CollectionsRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import hymnal.collections.create.CreateCollectionScreen.Event as UiEvent
import hymnal.collections.create.CreateCollectionScreen.State as UiState

@Inject
class CreateCollectionPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: CreateCollectionScreen,
    private val repository: CollectionsRepository
) : Presenter<UiState> {

    @Composable
    override fun present(): UiState {
        val coroutineScope = rememberStableCoroutineScope()
        var selectedColor by rememberRetained { mutableStateOf(CollectionColor.skyBlue) }
        var saveEnabled by rememberRetained { mutableStateOf(false) }

        return UiState(
            showUpNavigation = screen.showUpNavigation,
            selectedColor = selectedColor,
            saveEnabled = saveEnabled,
        ) { event ->
            when (event) {
                is UiEvent.OnNavigateUp -> navigator.pop()
                is UiEvent.OnTitleChanged -> {
                    saveEnabled = event.title.isNotBlank()
                }
                is UiEvent.OnColorSelected -> selectedColor = event.color
                is UiEvent.SaveClicked -> {
                    saveEnabled = false

                    val title = event.title.trim().toString()
                    val description = event.description?.trim().toString().takeUnless { it.isEmpty() }

                    coroutineScope.launch {
                        repository.create(
                            title = title,
                            description = description,
                            color = selectedColor.hex,
                        ).onSuccess {
                            navigator.pop()
                        }.onFailure {
                            Timber.e(it, "Error creating collection")
                            saveEnabled = true
                        }
                    }

                }
            }
        }
    }

    @CircuitInject(CreateCollectionScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator, screen: CreateCollectionScreen): CreateCollectionPresenter
    }
}