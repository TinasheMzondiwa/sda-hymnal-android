package app.hymnal.ui

import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.hymnal.ui.home.HomeScreen
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import hymnal.ui.extensions.LocalWindowWidthSizeClass
import hymnal.ui.theme.HymnalTheme

@Composable
fun HymnalApp(
    circuit: Circuit,
    windowWidthSizeClass: WindowWidthSizeClass
) {
    HymnalTheme {
        CompositionLocalProvider(
            LocalWindowWidthSizeClass provides windowWidthSizeClass,
        ) {
            Surface {
                ContentWithOverlays {
                    val backstack = rememberSaveableBackStack(HomeScreen)
                    val navigator = rememberCircuitNavigator(backstack)

                    NavigableCircuitContent(
                        navigator,
                        backstack,
                        circuit = circuit,
                    )
                }
            }
        }
    }
}