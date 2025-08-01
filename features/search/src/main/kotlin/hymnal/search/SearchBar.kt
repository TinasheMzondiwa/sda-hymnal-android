package hymnal.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import hymnal.ui.extensions.LocalWindowWidthSizeClass
import hymnal.ui.previews.DayNightPreviews
import hymnal.ui.theme.HymnalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnalSearchBar(
    openDrawer: () -> Unit = {}
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    val isExpandedScreen = LocalWindowWidthSizeClass.current == WindowWidthSizeClass.Expanded
    val alignment = when (LocalWindowWidthSizeClass.current) {
        WindowWidthSizeClass.Compact -> Alignment.TopCenter
        else -> Alignment.TopStart
    }

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .semantics { isTraversalGroup = true }
                .zIndex(1f)
                .fillMaxWidth()) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = { text = it },
                        onSearch = { active = false },
                        expanded = active,
                        onExpandedChange = {
                            active = it
                        },
                        placeholder = { Text("Search hymnal") },
                        leadingIcon = {
                            IconButton(onClick = {
                                if (active) {
                                    active = false
                                } else if (isExpandedScreen) {
                                    active = true
                                } else {
                                    openDrawer()
                                }
                            }) {
                                MenuToBackIcon(active, isExpandedScreen)
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = { }) {
                                Icon(Icons.Rounded.Mic, contentDescription = null)
                            }
                        },
                    )
                },
                expanded = active,
                onExpandedChange = {
                    active = it
                },
                modifier = Modifier.align(alignment),
                tonalElevation = 1.dp
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(4) { idx ->
                        val resultText = "Suggestion $idx"
                        ListItem(
                            headlineContent = { Text(resultText) },
                            supportingContent = { Text("Additional info") },
                            leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                            modifier = Modifier.clickable {
                                text = resultText
                                active = false
                            }
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun MenuToBackIcon(showBack: Boolean, isExpandedScreen: Boolean) {
    val (icon, contentDescription) = if (showBack) {
        Icons.AutoMirrored.Rounded.ArrowBack to "Back"
    } else {
        if (isExpandedScreen) {
            Icons.Rounded.Search to "Search"
        } else {
            Icons.Rounded.Menu to "Menu"
        }
    }
    Icon(
        imageVector = icon,
        contentDescription = contentDescription
    )
}


@Composable
@DayNightPreviews
private fun Preview() {
    HymnalTheme {
        Surface {
            HymnalSearchBar()
        }
    }
}