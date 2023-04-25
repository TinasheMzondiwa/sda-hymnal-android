package app.hymnal.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.hymnal.ui.home.HomeScreen.Event
import app.hymnal.ui.home.HomeScreen.State
import app.hymnal.ui.home.components.AppDrawer
import app.hymnal.ui.home.components.AppNavRail
import com.slack.circuit.codegen.annotations.CircuitInject
import hymnal.di.AppScope
import hymnal.ui.extensions.LocalWindowWidthSizeClass
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
@CircuitInject(HomeScreen::class, AppScope::class)
fun HomeUi(state: State, modifier: Modifier = Modifier) {
    val sink = state.eventSink

    val coroutineScope = rememberCoroutineScope()
    val isExpandedScreen = LocalWindowWidthSizeClass.current == WindowWidthSizeClass.Expanded
    val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen)

    ModalNavigationDrawer(
        drawerContent = {
            AppDrawer(
                currentRoute = state.currentRoute,
                navigateToHome = { sink(Event.OnNav(AppRoute.Hymnal)) },
                navigateToTopicalIndex = { sink(Event.OnNav(AppRoute.TopicalIndex)) },
                navigateToCollections = { sink(Event.OnNav(AppRoute.Collections)) },
                closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } },
                drawerHeaderContent = {
                    Spacer(modifier = Modifier.height(54.dp))
                }
            )
        },
        drawerState = sizeAwareDrawerState,
        // Only enable opening the drawer via gestures if the screen is not expanded
        gesturesEnabled = !isExpandedScreen,
        modifier = modifier
    ) {
        Row {
            if (isExpandedScreen) {
                AppNavRail(
                    currentRoute = state.currentRoute,
                    navigateToHome = { sink(Event.OnNav(AppRoute.Hymnal)) },
                    navigateToTopicalIndex = { sink(Event.OnNav(AppRoute.TopicalIndex)) },
                    navigateToCollections = { sink(Event.OnNav(AppRoute.Collections)) },
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(state) { targetState ->
                    Text(text = "State is $targetState")
                }
            }
        }
    }
}

/**
 * Determine the drawer state to pass to the modal drawer.
 */
@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        // If we want to allow showing the drawer, we use a real, remembered drawer
        // state defined above
        drawerState
    } else {
        // If we don't want to allow the drawer to be shown, we provide a drawer state
        // that is locked closed. This is intentionally not remembered, because we
        // don't want to keep track of any changes and always keep it closed
        DrawerState(DrawerValue.Closed)
    }
}