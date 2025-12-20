// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.ui

import android.app.Activity
import android.os.Build
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.sharedelements.SharedElementTransitionLayout
import com.slack.circuitx.android.rememberAndroidScreenAwareNavigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
import hymnal.sabbath.widget.sdk.isAtLeastApi
import hymnal.ui.theme.HymnalTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HymnalApp(
    circuit: Circuit,
    circuitNavigator: Navigator,
    backstack: SaveableBackStack,
    windowWidthSizeClass: WindowWidthSizeClass,
    isDarkTheme: Boolean,
    dynamicColor: Boolean,
) {
    HymnalTheme(
        darkTheme = isDarkTheme,
        dynamicColor = dynamicColor,
        windowWidthSizeClass = windowWidthSizeClass,
    ) {
        CircuitCompositionLocals(circuit = circuit) {
            SharedElementTransitionLayout {
                ContentWithOverlays {
                    val navigator =
                        rememberAndroidScreenAwareNavigator(
                            delegate = circuitNavigator,
                            context = LocalContext.current,
                        )

                    val decoratorFactory = remember(navigator) {
                        // Something strange happening on Android 14
                        if (isAtLeastApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)) {
                            GestureNavigationDecorationFactory(onBackInvoked = navigator::pop)
                        } else {
                            null
                        }
                    }

                    NavigableCircuitContent(
                        navigator = navigator,
                        backStack = backstack,
                        circuit = circuit,
                        decoratorFactory = decoratorFactory,
                    )
                }
            }
        }
    }

    SystemUiEffect(lightStatusBar = !isDarkTheme)
}

@Composable
private fun SystemUiEffect(
    lightStatusBar: Boolean,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
) {
    val localView = LocalView.current
    if (!localView.isInEditMode) {
        DisposableEffect(lightStatusBar) {
            val window = (localView.context as Activity).window
            WindowCompat.getInsetsController(window, localView)
                .isAppearanceLightStatusBars = lightStatusBar
            onDispose {
                WindowCompat.getInsetsController(
                    window,
                    localView
                ).isAppearanceLightStatusBars = !isSystemInDarkTheme
            }
        }
    }
}