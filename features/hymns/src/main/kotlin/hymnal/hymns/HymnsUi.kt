package hymnal.hymns

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.hymns.components.CategoryChip
import hymnal.libraries.navigation.HymnsScreen
import hymnal.ui.theme.HymnalTheme
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(HymnsScreen::class, AppScope::class)
@Composable
fun HymnsUi(state: State, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    val listState: LazyListState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column(Modifier.fillMaxWidth()) {
                        Text(stringResource(L10nR.string.hymns))

                        AnimatedVisibility(scrollBehavior.state.collapsedFraction == 0f) {
                            Spacer(
                                Modifier
                                    .padding(top = 16.dp)
                                    .size(48.dp, 4.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                },
                navigationIcon = {},
                actions = {
                    when (state) {
                        is State.Hymns -> {
                            IconButton(onClick = { state.eventSink(Event.OnSortClicked) }) {
                                Icon(
                                    state.sortType.icon,
                                    contentDescription = stringResource(state.sortType.title),
                                )
                            }
                        }

                        State.Loading -> Unit
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton({}) {
                Icon(Icons.Rounded.Dialpad, contentDescription = null)
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            contentPadding = innerPadding,
        ) {
            when (state) {
                is State.Hymns -> {
                    stickyHeader {
                        LazyRow(
                            contentPadding = PaddingValues(
                                start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                                top = 12.dp,
                                end = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                                bottom = 12.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.categories, key = { it.id }) { category ->
                                CategoryChip(
                                    selected = state.selectedCategory == category,
                                    category = category.name,
                                    onClick = {
                                        state.eventSink(Event.OnCategorySelected(category))
                                    },
                                )
                            }
                        }
                    }

                    items(state.hymns, key = { it.index }) { hymn ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .animateItem(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                hymn.number.toString(),
                                modifier = Modifier
                                    .sizeIn(minWidth = 48.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    )
                                    .padding(10.dp),
                                textAlign = TextAlign.Center,
                            )

                            Text(hymn.title)
                        }
                    }
                }

                State.Loading -> Unit
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        HymnsUi(State.Loading)
    }
}