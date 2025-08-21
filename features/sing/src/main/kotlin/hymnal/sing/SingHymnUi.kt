package hymnal.sing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.SingHymnScreen
import hymnal.sing.components.SingBottomAppBar
import hymnal.sing.components.SingTopAppBar
import hymnal.sing.components.hymnInfo
import hymnal.sing.components.hymnLyrics
import hymnal.ui.extensions.copy
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.scaffold.HazeScaffold

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(SingHymnScreen::class, AppScope::class)
@Composable
fun SingHymnUi(state: State, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    val listState: LazyListState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val bottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

    HazeScaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SingTopAppBar(
                onNavBack = {
                    (state as? State.Content)?.eventSink?.invoke(Event.OnNavBack)
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            when (state) {
                is State.Content -> {
                    var isPlaying by remember { mutableStateOf(false) }

                    SingBottomAppBar(
                        number = state.hymn.number,
                        isPlaying = isPlaying,
                        scrollBehavior = bottomAppBarScrollBehavior,
                        onPlayPause = {
                            isPlaying = !isPlaying
                        }
                    )
                }

                State.Loading -> Unit
            }
        },
        blurTopBar = true,
        blurBottomBar = true,
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection),
            state = listState,
            contentPadding = contentPadding.copy(
                layoutDirection = layoutDirection,
                start = contentPadding.calculateStartPadding(layoutDirection) + 16.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
                end = contentPadding.calculateEndPadding(layoutDirection) + 16.dp,
                bottom = 0.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            when (state) {
                is State.Content -> {
                    hymnInfo(
                        number = state.hymn.number,
                        title = state.hymn.title,
                        majorKey = state.hymn.majorKey,
                        author = null,
                    )
                    hymnLyrics(state.hymn.lyrics)
                }

                State.Loading -> {

                }
            }

            item {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }

            item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)) }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        SingHymnUi(state = State.Loading)
    }
}