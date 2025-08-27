package app.hymnal.ui

import android.app.Activity
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.hymnal.di.ActivityKey
import com.slack.circuit.foundation.Circuit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.services.prefs.HymnalPrefs
import hymnal.services.prefs.model.AppTheme
import hymnal.services.prefs.model.ThemeStyle
import kotlinx.coroutines.flow.map

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
class MainActivity(
    private val circuit: Circuit,
    private val prefs: HymnalPrefs,
) : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeStyle by prefs.themeStyle()
                .collectAsStateWithLifecycle(ThemeStyle())
            val appTheme = themeStyle.theme

            val isSystemInDarkTheme = isSystemInDarkTheme()
            val isDarkTheme by remember(appTheme) {
                derivedStateOf {
                    appTheme == AppTheme.DARK || (appTheme == AppTheme.FOLLOW_SYSTEM && isSystemInDarkTheme)
                }
            }

            HymnalApp(
                circuit = circuit,
                windowWidthSizeClass = calculateWindowSizeClass(this).widthSizeClass,
                isDarkTheme = isDarkTheme,
            )
        }
    }
}

