// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.hymnal.ui.home.HomeRoute
import app.hymnal.ui.home.HomeScreen
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.screen.Screen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.services.prefs.HymnalPrefs
import hymnal.services.prefs.model.AppTheme
import hymnal.services.prefs.model.ThemeStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import libraries.hymnal.di.ActivityKey
import timber.log.Timber

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
class MainActivity(
    private val circuit: Circuit,
    private val prefs: HymnalPrefs,
) : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeStyle: ThemeStyle? by prefs.themeStyle()
                .collectAsStateWithLifecycle(null)
            val appTheme = themeStyle?.theme
            val dynamicColors = themeStyle?.dynamicColors

            val isSystemInDarkTheme = isSystemInDarkTheme()
            val isDarkTheme by remember(appTheme) {
                derivedStateOf {
                    appTheme == AppTheme.DARK || (appTheme == AppTheme.FOLLOW_SYSTEM && isSystemInDarkTheme)
                }
            }

            val stackedScreens = parseDeepLink(intent) ?: persistentListOf(HomeScreen())

            HymnalApp(
                circuit = circuit,
                initialScreens = stackedScreens,
                windowWidthSizeClass = calculateWindowSizeClass(this).widthSizeClass,
                isDarkTheme = isDarkTheme,
                dynamicColor = dynamicColors ?: false,
            )

            splashScreen.setKeepOnScreenCondition { themeStyle == null }
        }
    }

    private fun parseDeepLink(intent: Intent): ImmutableList<Screen>? {
        val dataUri = intent.data ?: return null
        val screens = mutableListOf<Screen>()

        // Combine host and path segments to check for navigation targets
        val parts = mutableListOf<String>()
        dataUri.host?.let { parts.add(it) }
        parts.addAll(dataUri.pathSegments)

        parts.filter { it.isNotBlank() }.forEach { part ->
            when (part) {
                "sabbath" -> screens.add(HomeScreen(HomeRoute.Sabbath))
                "collections" -> screens.add(HomeScreen(HomeRoute.Collections))
                else -> Timber.d("Unknown deep-link part: $part")
            }
        }

        return screens.takeIf { it.isNotEmpty() }?.toImmutableList()
    }
}
