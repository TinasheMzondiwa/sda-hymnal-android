package hymnal.sing.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.services.content.HymnalContentProvider
import hymnal.sing.BottomBarState
import hymnal.sing.components.HymnContent
import kotlinx.coroutines.launch

@Stable
interface BottomBarStateProducer {
    @Composable
    operator fun invoke(hymn: HymnContent?, onIndex: (String) -> Unit): BottomBarState
}

@ContributesBinding(AppScope::class)
@Inject
class BottomBarStateProducerImpl(
    private val contentProvider: HymnalContentProvider,
) : BottomBarStateProducer {
    @Composable
    override fun invoke(hymn: HymnContent?, onIndex: (String) -> Unit): BottomBarState {
        val coroutineScope = rememberStableCoroutineScope()

        val state = rememberRetained(hymn) {
            BottomBarState(
                number = hymn?.number ?: 1,
                isPlaying = false,
                isPlayEnabled = hymn != null,
                previousEnabled = hymn?.let { it.number > 1 } ?: false,
                nextEnabled = hymn?.let { it.number < 695 } ?: false,
                eventSink = { event ->
                    when (event) {
                        BottomBarState.Event.OnGoToHymn -> Unit
                        BottomBarState.Event.OnNextHymn -> {
                            val current = hymn?.number ?: return@BottomBarState
                            coroutineScope.launch {
                                val number = current + 1
                                val next = contentProvider.hymn(number) ?: return@launch
                                onIndex(next.index)
                            }
                        }

                        BottomBarState.Event.OnPlayPause -> Unit
                        BottomBarState.Event.OnPreviousHymn -> {
                            val current = hymn?.number ?: return@BottomBarState
                            coroutineScope.launch {
                                val number = current - 1
                                val previous = contentProvider.hymn(number) ?: return@launch
                                onIndex(previous.index)
                            }
                        }
                    }
                }
            )
        }
        return state
    }
}