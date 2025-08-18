package hymnal.hymns

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.overlay.OverlayEffect
import dev.zacsweers.metro.AppScope
import hymnal.hymns.components.HymnCard
import hymnal.hymns.components.HymnsSearchBar
import hymnal.hymns.components.pad.NumberPadBottomSheet
import hymnal.hymns.components.previewHymn
import hymnal.libraries.navigation.HymnsScreen
import hymnal.services.model.HymnCategory
import hymnal.ui.extensions.copy
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.AvatarNavigationIcon
import hymnal.ui.widget.scaffold.HazeScaffold
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(HymnsScreen::class, AppScope::class)
@Composable
fun HymnsUi(state: State, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    val hapticFeedback = LocalAppHapticFeedback.current
    val listState: LazyListState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    HazeScaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                TopAppBar(
                    title = {
                        HymnsSearchBar(
                            results = state.searchResults,
                            onSearch = {
                                state.eventSink(Event.OnQueryChanged(it.toString()))
                            },
                            trailingIcon = {
                                AvatarNavigationIcon(
                                    photoUrl = "https://images.unsplash.com/photo-1570158268183-d296b2892211?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                                )
                            }
                        )
                    },
                    actions = {
                        IconButton(onClick = { state.eventSink(Event.OnSortClicked) }) {
                            Icon(
                                state.sortType.icon,
                                contentDescription = stringResource(state.sortType.title),
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                    )
                )

                if (state.categories.isNotEmpty()) {
                    PrimaryScrollableTabRow(
                        selectedTabIndex = state.categories.indexOf(state.selectedCategory),
                        containerColor = Color.Transparent,
                        edgePadding = 16.dp,
                        tabs = {
                            state.categories.forEachIndexed { index, category ->
                                Tab(
                                    selected = category == state.selectedCategory,
                                    onClick = { state.eventSink(Event.OnCategorySelected(category)) },
                                    text = {
                                        val title = if (index == 0) {
                                            category.name
                                        } else {
                                            "${category.name} (${category.start}–${category.end})"
                                        }
                                        Text(title)
                                    },
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    )
                }

            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    state.eventSink(Event.OnNumberPadClicked)
                    hapticFeedback.performScreenView()
                },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Rounded.Dialpad, contentDescription = null)
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        blurTopBar = true,
    ) { contentPadding ->
        LazyColumn(
            state = listState,
            contentPadding = contentPadding.copy(
                layoutDirection = layoutDirection,
                top = contentPadding.calculateTopPadding() + 12.dp,
            ),
        ) {
            items(state.hymns, key = { it.index }) { hymn ->
                HymnCard(
                    hymn = hymn,
                    sortType = state.sortType.next(),
                    modifier = Modifier.animateItem(),
                    onClick = { state.eventSink(Event.OnHymnClicked(hymn.index)) },
                )
            }

            // Fab clearance space
            item {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                )
            }

            item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)) }
        }
    }

    OverlayContent(state.overlayState)

    LaunchedEffect(state.sortType, state.selectedCategory) {
        // Reset the list state when sort type or selected category changes
        listState.animateScrollToItem(0)
    }
}

@Composable
private fun OverlayContent(state: OverlayState?) {
    OverlayEffect(state) {
        when (state) {
            is OverlayState.NumberPadSheet -> state.onResult(
                show(NumberPadBottomSheet())
            )
            null -> Unit
        }
    }
}

private val previewCategories = persistentListOf(
    HymnCategory("1", "Category 1", 1, 2),
    HymnCategory("2", "Category 2", 2, 3),
    HymnCategory("2", "Category 3", 3, 4)
)

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        HymnsUi(
            State(
                sortType = SortType.NUMBER,
                selectedCategory = previewCategories.first(),
                categories = previewCategories,
                hymns = persistentListOf(previewHymn, previewHymn.copy(index = "2")),
                searchResults = persistentListOf(),
                overlayState = null,
                eventSink = {},
            )
        )
    }
}