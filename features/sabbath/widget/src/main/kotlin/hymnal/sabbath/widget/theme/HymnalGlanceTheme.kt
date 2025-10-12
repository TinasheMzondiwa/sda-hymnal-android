package hymnal.sabbath.widget.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.glance.GlanceComposable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import hymnal.sabbath.widget.sdk.isAtLeastApi
import hymnal.ui.theme.color.DarkColorScheme
import hymnal.ui.theme.color.LightColorScheme

@Composable
fun HymnalGlanceTheme(
    content: @Composable @GlanceComposable () -> Unit
) {
    GlanceTheme(
        colors = if (isAtLeastApi(Build.VERSION_CODES.S)) {
            GlanceTheme.colors
        } else {
            ColorProviders(
                light = LightColorScheme,
                dark = DarkColorScheme
            )
        },
        content = content
    )
}

