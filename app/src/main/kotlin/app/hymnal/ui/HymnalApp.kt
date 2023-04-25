package app.hymnal.ui

import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.hymnal.ui.home.HomeScreen
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.CircuitConfig
import com.slack.circuit.foundation.LocalCircuitConfig
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.push
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import hymnal.ui.extensions.LocalWindowWidthSizeClass
import hymnal.ui.theme.HymnalTheme

@Composable
fun HymnalApp(
    circuitConfig: CircuitConfig,
    windowWidthSizeClass: WindowWidthSizeClass
) {
    HymnalTheme {
        CircuitCompositionLocals(circuitConfig) {
            CompositionLocalProvider(
                LocalWindowWidthSizeClass provides windowWidthSizeClass,
                LocalCircuitConfig provides circuitConfig,
            ) {
                Surface {
                    ContentWithOverlays {
                        val backstack = rememberSaveableBackStack { push(HomeScreen) }
                        val navigator = rememberCircuitNavigator(backstack)

                        NavigableCircuitContent(navigator, backstack)
                    }
                }
            }
        }
    }
}