package hymnal.hymns.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import hymnal.ui.extensions.LocalWindowWidthSizeClass
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnsSearchBar(
    onSubmit: (CharSequence) -> Unit,
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

    val inputField =
        @Composable {
            InputField(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = {
                    onSubmit(textFieldState.text)
                    scope.launch { searchBarState.animateToCollapsed() }
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                placeholder = { Text("Search Hymnal") },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
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
                },
                trailingIcon = {
                    AnimatedContent(targetState = searchBarState.targetValue) { state ->
                        when (state) {
                            SearchBarValue.Collapsed -> trailingIcon()
                            SearchBarValue.Expanded -> {
                                IconButton(onClick = {}) {
                                    Icon(Icons.Rounded.Mic, null)
                                }
                            }
                        }
                    }
                },
            )
        }

    TopSearchBar(
        scrollBehavior = scrollBehavior,
        state = searchBarState,
        inputField = inputField,
    )

    if (LocalWindowWidthSizeClass.current == WindowWidthSizeClass.Compact) {
        ExpandedFullScreenSearchBar(state = searchBarState, inputField = inputField) {}
    } else {
        ExpandedDockedSearchBar(state = searchBarState, inputField = inputField) { }
    }
}