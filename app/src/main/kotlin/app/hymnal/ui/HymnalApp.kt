// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.ui

import android.app.Activity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import app.hymnal.ui.home.HomeScreen
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.sharedelements.SharedElementTransitionLayout
import com.slack.circuitx.android.rememberAndroidScreenAwareNavigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
import hymnal.ui.extensions.LocalWindowWidthSizeClass
import hymnal.ui.theme.HymnalTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HymnalApp(
    circuit: Circuit,
    windowWidthSizeClass: WindowWidthSizeClass,
    isDarkTheme: Boolean,
    dynamicColor: Boolean,
) {
    HymnalTheme(darkTheme = isDarkTheme, dynamicColor = dynamicColor) {
        CompositionLocalProvider(
            LocalWindowWidthSizeClass provides windowWidthSizeClass,
        ) {
            CircuitCompositionLocals(circuit = circuit) {
                SharedElementTransitionLayout {
                    ContentWithOverlays {
                        val backstack = rememberSaveableBackStack(HomeScreen)
                        val circuitNavigator = rememberCircuitNavigator(backstack)
                        val navigator = rememberAndroidScreenAwareNavigator(circuitNavigator, LocalContext.current)

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

    SystemUiEffect(lightStatusBar = !isDarkTheme)
}

@Composable
private fun SystemUiEffect(
    lightStatusBar: Boolean,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val localView = LocalView.current
    if (!localView.isInEditMode) {
        DisposableEffect(lightStatusBar) {
            val window = (localView.context as Activity).window
            WindowCompat.getInsetsController(window, localView).isAppearanceLightStatusBars = lightStatusBar
            onDispose { WindowCompat.getInsetsController(window, localView).isAppearanceLightStatusBars = !isSystemInDarkTheme }
        }
    }
}