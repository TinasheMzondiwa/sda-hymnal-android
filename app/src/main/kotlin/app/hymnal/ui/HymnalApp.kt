package app.hymnal.ui

import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import app.hymnal.ui.home.HomeScreen
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
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
            CircuitCompositionLocals(circuit = circuit) {
                ContentWithOverlays {
                    val backstack = rememberSaveableBackStack(HomeScreen)
                    val navigator = rememberCircuitNavigator(backstack)

                    NavigableCircuitContent(
                        navigator = navigator,
                        backStack = backstack,
                        circuit = circuit,
                        decoratorFactory =
                            remember(navigator) {
                                GestureNavigationDecorationFactory(onBackInvoked = navigator::pop)
                            },
                    )
                }
            }
        }
    }
}