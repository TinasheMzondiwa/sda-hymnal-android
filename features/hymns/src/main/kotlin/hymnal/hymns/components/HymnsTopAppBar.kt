// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hymnal.hymns.Event
import hymnal.hymns.State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnsTopAppBar(
    state: State,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                HymnsSearchBar(
                    results = state.searchResults,
                    onSearch = {
                        state.eventSink(Event.OnQueryChanged(it.toString()))
                    },
                    onResultClick = {
                        state.eventSink(Event.OnSearchResultClicked(it))
                    },
                )
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
                edgePadding = 0.dp,
                tabs = {
                    state.categories.forEachIndexed { index, category ->
                        AnimatedVisibility(true) {
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
                }
            )
        }
    }
}