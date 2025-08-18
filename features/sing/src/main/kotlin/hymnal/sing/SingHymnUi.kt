package hymnal.sing

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.SingHymnScreen

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(SingHymnScreen::class, AppScope::class)
@Composable
fun SingHymnUi(state: State, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    val title = when (state) {
                        is State.Content -> state.hymn.title
                        State.Loading -> ""
                    }
                    Text(text = title)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (state as? State.Content)?.eventSink?.invoke(Event.OnNavBack)
                    }) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null) }
                }
            )
        }
    ) { contentPadding ->
        LazyColumn(contentPadding = contentPadding) {

        }
    }
}