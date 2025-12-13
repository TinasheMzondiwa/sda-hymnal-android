// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.immersive

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
import hymnal.services.content.HymnalContentProvider
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics
import hymnal.sing.immersive.state.TopBarStateProducer
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import hymnal.sing.immersive.ImmersiveContentScreen.State as UiState

@AssistedInject
class ImmersiveContentPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: ImmersiveContentScreen,
    private val contentProvider: HymnalContentProvider,
    private val topBarStateProducer: TopBarStateProducer,
) : Presenter<UiState> {

    @Composable
    override fun present(): UiState {
        var currentIndex by rememberRetained { mutableStateOf(screen.hymnId) }
        val currentHymn by produceRetainedState<Hymn?>(null, key1 = currentIndex) {
            contentProvider.hymn(currentIndex)
                .catch { Timber.e(it) }
                .collect { value = it }
        }
        val pages = rememberRetained(currentHymn) {
            currentHymn?.toPages()?.toImmutableList() ?: persistentListOf()
        }

        val topBarState = topBarStateProducer(navigator, currentHymn) { currentIndex = it }

        return UiState(
            showControls = screen.showControls,
            topBarState = topBarState,
            pages = pages,
        )
    }

    private fun Hymn.toPages(): List<ContentPage> = buildList {
        add(
            ContentPage(
                lines = persistentListOf(
                    "$number - $title",
                    majorKey?.let { "Key: $it" } ?: "",
                )
            )
        )

        lyrics.forEach { lyric ->
            val title: String
            val lines: List<String>

            when (lyric) {
                is HymnLyrics.Chorus -> {
                    title = "Chorus"
                    lines = lyric.lines
                }
                is HymnLyrics.Verse -> {
                    title = "${lyric.index}"
                    lines = lyric.lines
                }
            }

            add(
                ContentPage(
                    lines = (persistentListOf(title) + lines).toImmutableList()
                )
            )
        }
    }

    @CircuitInject(ImmersiveContentScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator, screen: ImmersiveContentScreen): ImmersiveContentPresenter
    }
}