package hymnal.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import hymnal.ui.haptics.DefaultHapticFeedback
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.color.DarkColorScheme
import hymnal.ui.theme.color.LightColorScheme
import hymnal.ui.theme.type.HymnalTypography

@Composable
fun HymnalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalAppHapticFeedback provides DefaultHapticFeedback(LocalHapticFeedback.current),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = HymnalTypography,
            content = content
        )
    }
}