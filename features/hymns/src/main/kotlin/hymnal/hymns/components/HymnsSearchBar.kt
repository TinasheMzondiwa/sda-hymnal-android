// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.ui.extensions.LocalWindowWidthSizeClass
import hymnal.ui.theme.HymnalTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnsSearchBar(
    results: ImmutableList<SearchResult>,
    onSearch: (CharSequence) -> Unit,
    onResultClick: (SearchResult) -> Unit = {},
    trailingIcon: @Composable () -> Unit = {}
) {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    // Clear the text field when the search bar collapses
    LaunchedEffect(searchBarState.targetValue) {
        if (searchBarState.targetValue == SearchBarValue.Collapsed) {
            textFieldState.clearText()
        }
    }

    LaunchedEffect(textFieldState.text) {
        onSearch(textFieldState.text)
    }

    val inputField =
        @Composable {
            InputField(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                placeholder = { Text("Search Hymnal") },
                leadingIcon = {
                    AnimatedContent(targetState = searchBarState.currentValue) { searchBarValue ->
                        if (searchBarValue == SearchBarValue.Expanded) {
                            TooltipBox(
                                positionProvider =
                                    TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Above
                                    ),
                                tooltip = { PlainTooltip { Text("Back") } },
                                state = rememberTooltipState(),
                            ) {
                                IconButton(
                                    onClick = {
                                        scope.launch { searchBarState.animateToCollapsed() }
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = "Back",
                                    )
                                }
                            }
                        } else {
                            Icon(
                                Icons.Rounded.Search,
                                contentDescription = null
                            )
                        }
                    }
                },
                trailingIcon = {
                    AnimatedContent(targetState = searchBarState.targetValue) { state ->
                        when (state) {
                            SearchBarValue.Collapsed -> trailingIcon()
                            SearchBarValue.Expanded -> {
                                AnimatedContent(textFieldState.text.isEmpty()) { isEmpty ->
                                    if (isEmpty) {
                                        IconButton(onClick = {}) {
                                            Icon(Icons.Rounded.Mic, null)
                                        }
                                    } else {
                                        IconButton(onClick = { textFieldState.clearText() }) {
                                            Icon(Icons.Rounded.Clear, null)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
            )
        }

    AppBarWithSearch(
        scrollBehavior = scrollBehavior,
        state = searchBarState,
        inputField = inputField,
        colors = SearchBarDefaults.appBarWithSearchColors(appBarContainerColor = Color.Transparent),
    )

    if (LocalWindowWidthSizeClass.current == WindowWidthSizeClass.Compact) {
        ExpandedFullScreenSearchBar(
            state = searchBarState,
            inputField = inputField,
        ) { SearchResults(results, onResultClick) }
    } else {
        ExpandedDockedSearchBar(state = searchBarState, inputField = inputField) {
            SearchResults(results, onResultClick)
        }
    }
}

@Composable
private fun SearchResults(
    results: ImmutableList<SearchResult>,
    onResultClick: (SearchResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(top = 16.dp),
    ) {
        items(results, key = { it.index }) { result ->
            SearchResultContent(
                result = result,
                modifier = Modifier
                    .animateItem()
                    .clickable { onResultClick(result) }
            )
        }

        item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)) }
    }

    LaunchedEffect(results) {
        listState.requestScrollToItem(0)
    }
}

@Composable
private fun SearchResultContent(result: SearchResult, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(result.title) },
        leadingContent = {
            NumberText(
                number = result.number,
                modifier = Modifier
            )
        },
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            SearchResultContent(
                result = SearchResult(
                    index = "1",
                    title = "Amazing Grace",
                    number = 1
                ),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}