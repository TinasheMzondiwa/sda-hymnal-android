package hymnal.ui.widget.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun HazeNavigationSuiteScaffold(
    navigationSuiteItems: NavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationSuiteScaffoldDefaults.containerColor,
    contentColor: Color = NavigationSuiteScaffoldDefaults.contentColor,
    state: NavigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState(),
    hazeState: HazeState = remember { HazeState() },
    hazeStyle: HazeStyle = HazeMaterials.regular(MaterialTheme.colorScheme.surface),
    content: @Composable () -> Unit = {},
) {
    val layoutType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    val navigationSuiteColors = NavigationSuiteDefaults.colors(
        shortNavigationBarContainerColor = Color.Transparent,
        navigationBarContainerColor = Color.Transparent,
    )

    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor
    ) {
        NavigationSuiteScaffoldLayout(
            navigationSuite = {
                when (layoutType) {
                    NavigationSuiteType.NavigationBar -> {
                        Box(
                            modifier = Modifier.hazeEffect(state = hazeState, style = hazeStyle)
                        ) {
                            NavigationSuite(
                                layoutType = layoutType,
                                colors = navigationSuiteColors,
                                content = navigationSuiteItems,
                            )
                        }
                    }

                    NavigationSuiteType.NavigationRail -> {
                        Row {
                            NavigationSuite(
                                layoutType = layoutType,
                                colors = navigationSuiteColors,
                                content = navigationSuiteItems,
                            )

                            VerticalDivider()
                        }
                    }
                    else -> {
                        NavigationSuite(
                            layoutType = layoutType,
                            colors = navigationSuiteColors,
                            content = navigationSuiteItems,
                        )
                    }
                }
            },
            state = state,
            layoutType = layoutType,
            content = {
                Box(
                    Modifier
                        .hazeSource(state = hazeState)
                        .consumeWindowInsets(
                            if (
                                state.currentValue == NavigationSuiteScaffoldValue.Hidden &&
                                !state.isAnimating
                            ) {
                                NoWindowInsets
                            } else {
                                when (layoutType) {
                                    NavigationSuiteType.NavigationBar ->
                                        NavigationBarDefaults.windowInsets.only(
                                            WindowInsetsSides.Bottom
                                        )

                                    NavigationSuiteType.NavigationRail ->
                                        NavigationRailDefaults.windowInsets.only(
                                            WindowInsetsSides.Start
                                        )

                                    NavigationSuiteType.NavigationDrawer ->
                                        DrawerDefaults.windowInsets.only(WindowInsetsSides.Start)

                                    else -> NoWindowInsets
                                }
                            }
                        )
                ) {
                    content()
                }
            },
        )
    }
}

private val NoWindowInsets = WindowInsets(0, 0, 0, 0)

