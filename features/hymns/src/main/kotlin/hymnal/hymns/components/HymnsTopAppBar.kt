// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hymnal.hymns.Event
import hymnal.hymns.State
import hymnal.ui.widget.AvatarNavigationIcon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnsTopAppBar(
    state: State,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    Column(
        modifier = modifier
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
                    onResultClick = {
                        state.eventSink(Event.OnSearchResultClicked(it))
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
}