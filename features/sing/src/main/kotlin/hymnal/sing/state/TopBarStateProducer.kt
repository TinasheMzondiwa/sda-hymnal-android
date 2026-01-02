// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.state

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuitx.android.IntentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.navigation.AddToCollectionScreen
import hymnal.libraries.navigation.AppHomeScreen
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.services.content.repository.CollectionsRepository
import hymnal.services.model.HymnLyrics
import hymnal.sing.SingOverlayState
import hymnal.sing.TopBarState
import hymnal.sing.components.HymnContent
import hymnal.sing.components.text.TextStyleScreen
import hymnal.sing.immersive.ImmersiveContentActivity
import kotlinx.coroutines.flow.catch
import timber.log.Timber

@Stable
interface TopBarStateProducer {
    @Composable
    operator fun invoke(
        navigator: Navigator,
        hymn: HymnContent?,
        source: SingHymnScreen.Source
    ): TopBarState
}

@Inject
@ContributesBinding(scope = AppScope::class)
class TopBarStateProducerImpl(
    private val collectionsRepository: CollectionsRepository
) : TopBarStateProducer {
    @Composable
    override fun invoke(
        navigator: Navigator,
        hymn: HymnContent?,
        source: SingHymnScreen.Source
    ): TopBarState {
        val hymnId = hymn?.index
        val collections by produceRetainedState(emptyList(), hymnId) {
            hymnId?.let { id ->
                collectionsRepository.getHymnCollections(id)
                    .catch { Timber.e(it) }
                    .collect { value = it }
            }
        }
        var overlayState by rememberRetained { mutableStateOf<SingOverlayState?>(null) }

        return TopBarState(
            isSavedToCollection = collections.isNotEmpty(),
            overlayState = overlayState,
            eventSink = { event ->
                when (event) {
                    is TopBarState.Event.OnNavBack -> {
                        when (source) {
                            SingHymnScreen.Source.DEEP_LINK -> navigator.goTo(AppHomeScreen)
                            else -> navigator.pop()
                        }
                    }
                    is TopBarState.Event.OnFullscreenClick -> {
                        hymnId?.let {
                            val intent = ImmersiveContentActivity.launchIntent(event.context, it)
                            navigator.goTo(IntentScreen(intent))
                        }
                    }
                    TopBarState.Event.OnSaveClick -> {
                        hymnId?.let {
                            overlayState =
                                SingOverlayState.BottomSheet(
                                    screen = AddToCollectionScreen(it),
                                    skipPartiallyExpanded = true,
                                ) { overlayState = null }
                        }
                    }
                    is TopBarState.Event.OnShareClick -> hymn?.let {
                        shareHymnText(
                            event.context,
                            it
                        )
                    }
                    TopBarState.Event.OnStyleClick -> {
                        overlayState =
                            SingOverlayState.BottomSheet(
                                screen = TextStyleScreen,
                                skipPartiallyExpanded = true,
                            ) { overlayState = null }
                    }
                }
            }
        )
    }
}

private fun shareHymnText(context: Context, hymn: HymnContent) {
    var chorusAdded = false
    val shareText = buildString {
        append(hymn.number)
        append(" - ")
        append(hymn.title)
        append("\n\n")
        hymn.lyrics.forEach { verse ->
            when (verse) {
                is HymnLyrics.Chorus -> {
                    if (!chorusAdded) {
                        append("Chorus:\n")
                        verse.lines.forEach {
                            append(it)
                            append("\n")
                        }
                        append("\n")
                        chorusAdded = true
                    }
                }
                is HymnLyrics.Verse -> {
                    verse.lines.forEach {
                        append(it)
                        append("\n")
                    }
                    append("\n")
                }
            }

        }
        append("\n\n")
    }

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share Hymn")
    if (sendIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(shareIntent)
    }
}