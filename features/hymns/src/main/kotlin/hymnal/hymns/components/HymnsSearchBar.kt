// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import hymnal.libraries.navigation.key.SearchSharedTransitionKey
import hymnal.ui.extensions.LocalWindowWidthSizeClass
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.hymn.NumberText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnsSearchBar(
    results: ImmutableList<SearchResult>,
    onSearch: (CharSequence) -> Unit,
    modifier: Modifier = Modifier,
    onResultClick: (SearchResult) -> Unit = {},
    trailingIcon: @Composable () -> Unit = {}
) {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val hapticFeedback = LocalAppHapticFeedback.current

    // Launcher for voice input
    val voiceInputLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val recognitionResults = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            recognitionResults?.firstOrNull()?.let { spokenText ->
                textFieldState.setTextAndPlaceCursorAtEnd(spokenText)
            }
        }
    }

    val voicePrompt = stringResource(id = L10nR.string.search_prompt_voice)
    val voiceIntent: Intent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_PROMPT, voicePrompt)
        }
    }


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
                placeholder = { Text(stringResource(id = L10nR.string.search_hymnal)) },
                leadingIcon = {
                    AnimatedContent(targetState = searchBarState.currentValue) { searchBarValue ->
                        if (searchBarValue == SearchBarValue.Expanded) {
                            TooltipBox(
                                positionProvider =
                                    TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Above
                                    ),
                                tooltip = { PlainTooltip { Text(stringResource(id = L10nR.string.back)) } },
                                state = rememberTooltipState(),
                            ) {
                                IconButton(
                                    onClick = {
                                        scope.launch { searchBarState.animateToCollapsed() }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = stringResource(id = L10nR.string.back),
                                    )
                                }
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Search,
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
                                        IconButton(onClick = {
                                            hapticFeedback.performClick()
                                            try {
                                                voiceInputLauncher.launch(voiceIntent)
                                            } catch (e: ActivityNotFoundException) {
                                                Timber.e(e, "Voice recognition not available")
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Mic,
                                                contentDescription = stringResource(id = L10nR.string.voice_search)
                                            )
                                        }
                                    } else {
                                        IconButton(onClick = {
                                            hapticFeedback.performClick()
                                            textFieldState.clearText()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Clear,
                                                contentDescription = stringResource(id = L10nR.string.clear_search)
                                            )
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
        modifier = modifier,
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
        if (results.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchBarButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    SharedElementTransitionScope {
        Surface(
            modifier =
                modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(SearchBarDefaults.windowInsets)
                    .semantics { isTraversalGroup = true },
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .sharedElement(
                        sharedContentState =
                            rememberSharedContentState(
                                SearchSharedTransitionKey(
                                    type = SearchSharedTransitionKey.ElementType.Button,
                                )
                            ),
                        animatedVisibilityScope =
                            requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                    )
                    .fillMaxWidth()
                    .padding(start = 6.dp, end = 18.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = SearchBarDefaults.inputFieldShape
                    )
                    .clip(SearchBarDefaults.inputFieldShape)
                    .clickable(onClick = onClick, role = Role.Button)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    modifier = Modifier.sharedBounds(
                        sharedContentState =
                            rememberSharedContentState(
                                SearchSharedTransitionKey(
                                    id = SearchSharedTransitionKey.ICON_ID,
                                    type = SearchSharedTransitionKey.ElementType.Icon,
                                )
                            ),
                        animatedVisibilityScope =
                            requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                    )
                )

                Text(
                    text = stringResource(id = L10nR.string.search_hymnal),
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState =
                                rememberSharedContentState(
                                    SearchSharedTransitionKey(
                                        id = SearchSharedTransitionKey.FIELD_ID,
                                        type = SearchSharedTransitionKey.ElementType.TextField,
                                    )
                                ),
                            animatedVisibilityScope =
                                requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                        )
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun PreviewSearchButton() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            Surface {
                SearchBarButton(
                    onClick = {},
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}