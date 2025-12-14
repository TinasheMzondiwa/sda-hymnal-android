// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.search

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.SearchScreen
import hymnal.libraries.navigation.key.SearchSharedTransitionKey
import hymnal.ui.extensions.plus
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.scaffold.HazeScaffold
import kotlinx.collections.immutable.persistentListOf
import timber.log.Timber
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalHazeMaterialsApi::class
)
@CircuitInject(SearchScreen::class, AppScope::class)
@Composable
fun SearchUiScreen(state: State, modifier: Modifier = Modifier) {
    val hapticFeedback = LocalAppHapticFeedback.current
    val scrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val containerColor = MaterialTheme.colorScheme.surfaceContainerHigh

    var textFieldValueState by remember(state.query) {
        mutableStateOf(
            TextFieldValue(
                text = state.query,
                selection = TextRange(state.query.length)
            )
        )
    }

    SharedElementTransitionScope {
        HazeScaffold(
            modifier = modifier
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
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        SearchTextField(
                            textFieldValueState = textFieldValueState,
                            onValueChange = {
                                textFieldValueState = it
                                state.eventSink(Event.OnQueryChange(it.text))
                            },
                            modifier = Modifier
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                hapticFeedback.performClick()
                                state.eventSink(Event.OnNavigateUp)
                            },
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
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(L10nR.string.nav_back)
                            )
                        }
                    },
                    actions = {
                        SearchTrailingIcon(
                            state = if (textFieldValueState.text.isEmpty()) {
                                SearchState.Empty
                            } else {
                                SearchState.Results
                            },
                            onVoiceQuery = { query ->
                                textFieldValueState = TextFieldValue(
                                    text = query,
                                    selection = TextRange(query.length)
                                )
                                state.eventSink(Event.OnQueryChange(query))
                            },
                            onClearQuery = {
                                hapticFeedback.performError()
                                state.eventSink(Event.OnClearQuery)
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                    ),
                    scrollBehavior = scrollBehavior,
                )
            },
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onSurface,
            blurTopBar = true,
            hazeStyle = HazeMaterials.ultraThin(containerColor),
        ) { contentPadding ->
            SearchContent(state, contentPadding)
        }
    }

}

@Composable
private fun SearchTextField(
    textFieldValueState: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        AnimatedVisibility(
            visible = textFieldValueState.text.isEmpty(),
            modifier = Modifier.fillMaxWidth(),
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 150)),
        ) {
            Text(
                text = stringResource(L10nR.string.search_query),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = LocalTextStyle.current,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        BasicTextField(
            value = textFieldValueState,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
        )

    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun SearchContent(
    state: State,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current
    val hapticFeedback = LocalAppHapticFeedback.current

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding.plus(
            layoutDirection = layoutDirection,
            top = 12.dp,
        ),
    ) {
        when (state) {
            is State.Empty -> {
                item("empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(L10nR.string.search_results_empty))
                    }
                }
            }

            is State.RecentHymns -> {

                if (state.hymns.isNotEmpty()) {
                    item("recent-hymns") {
                        Row(
                            modifier = Modifier
                                .animateItem()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = stringResource(L10nR.string.recent_hymns),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                items(items = state.hymns, key = { it.index }) { hymn ->
                    HymnResult(
                        hymn = hymn,
                        modifier = Modifier.animateItem(),
                        onClick = { state.eventSink(Event.OnHymnClick(hymn)) },
                    )
                }
            }

            is State.SearchResults -> {
                item("results-filters") {
                    if (state.filters.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp)
                        ) {
                            items(state.filters, { it.year }) { filter ->
                                val selected = filter.selected

                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        hapticFeedback.performToggleSwitch(!filter.selected)
                                        state.eventSink(Event.OnFilterChange(filter))
                                    },
                                    label = {
                                        Text(
                                            text = filter.label,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    },
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selected,
                                        selectedBorderColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }

                items(items = state.hymns, key = { it.index }) { hymn ->
                    HymnResult(
                        hymn = hymn,
                        modifier = Modifier.animateItem(),
                        onClick = { state.eventSink(Event.OnHymnClick(hymn)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTrailingIcon(
    state: SearchState,
    modifier: Modifier = Modifier,
    onVoiceQuery: (String) -> Unit = {},
    onClearQuery: () -> Unit = {},
) {
    val hapticFeedback = LocalAppHapticFeedback.current
    // Launcher for voice input
    val voiceInputLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val recognitionResults = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            recognitionResults?.firstOrNull()?.trim()?.takeIf { it.isNotBlank() }?.let { spokenText ->
                onVoiceQuery(spokenText)
            }
        }
    }

    val voicePrompt = stringResource(id = L10nR.string.search_prompt_voice)
    val voiceIntent: Intent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_PROMPT, voicePrompt)
        }
    }

    AnimatedContent(targetState = state, modifier = modifier) { targetState ->
        when (targetState) {
            SearchState.Empty -> {
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
            }

            SearchState.Results -> {
                IconButton(onClick = {
                    hapticFeedback.performClick()
                    onClearQuery()
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

private enum class SearchState { Empty, Results }

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            SearchUiScreen(
                State.RecentHymns(
                    hymns = persistentListOf(previewHymn),
                    query = "sing"
                ) {})
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun PreviewResults() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            SearchUiScreen(
                State.SearchResults(
                    filters = persistentListOf(
                        FilterItem(selected = true, label = "New hymnal", year = "1985"),
                        FilterItem(selected = false, label = "Old hymnal", year = "1941"),
                        FilterItem(selected = false, label = "Choruses", year = "chorus")
                    ),
                    hymns = persistentListOf(previewHymn), query = "sing"
                ) {}
            )
        }
    }
}