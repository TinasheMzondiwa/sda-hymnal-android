package hymnal.hymns

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.hymns.components.CategoryChip
import hymnal.libraries.navigation.HymnsScreen
import hymnal.ui.theme.HymnalTheme

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(HymnsScreen::class, AppScope::class)
@Composable
fun HymnsUi(state: State, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Hymns") },
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
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