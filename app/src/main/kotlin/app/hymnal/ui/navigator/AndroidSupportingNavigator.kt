package app.hymnal.ui.navigator

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.browser.customtabs.CustomTabsIntent
import app.hymnal.R
import app.hymnal.ui.home.HomeScreen
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.navigation.AppHomeScreen
import hymnal.libraries.navigation.BrowserIntentScreen
import timber.log.Timber

@AssistedInject
class AndroidSupportingNavigator(
    @Assisted private val navigator: Navigator,
    @Assisted private val activity: ComponentActivity
) : Navigator by navigator {

    override fun goTo(screen: Screen): Boolean {
        return when (screen) {
            is BrowserIntentScreen -> false.also { activity.launchWebUrl(screen.url) }
            AppHomeScreen -> false.also { navigator.resetRoot(HomeScreen()) }
            else -> navigator.goTo(screen)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator, activity: ComponentActivity): AndroidSupportingNavigator
    }
}

private fun ComponentActivity.launchWebUrl(uri: Uri): Boolean {
    try {
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .setStartAnimations(this, R.anim.slide_up, android.R.anim.fade_out)
            .setExitAnimations(this, android.R.anim.fade_in, R.anim.slide_down)
            .build()
        intent.launchUrl(this, uri)
        return true
    } catch (ignored: Exception) {
        Timber.e(ignored)
    }

    // Fall back to launching a default web browser intent.
    try {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            return true
        }
    } catch (ignored: Exception) {
        Timber.e(ignored)
    }

    // We were unable to show the web page.
    return false
}

