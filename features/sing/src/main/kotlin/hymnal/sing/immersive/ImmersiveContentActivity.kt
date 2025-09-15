// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.immersive

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitContent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.ui.theme.HymnalTheme
import libraries.hymnal.di.ActivityKey

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(ImmersiveContentActivity::class)
@Inject
class ImmersiveContentActivity(
    private val circuit: Circuit
) : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val hymnId = intent?.getStringExtra(HYMN_ID) ?: return finish()

        val contentView = findViewById<View>(android.R.id.content)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        fun onClickStuff() {
            contentView.performClick()
        }

        // Add a listener to update the behavior of the toggle fullscreen button when
        // the system bars are hidden or revealed.
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            // You can hide the caption bar even when the other system bars are visible.
            // To account for this, explicitly check the visibility of navigationBars()
            // and statusBars() rather than checking the visibility of systemBars().
            if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())
            ) {
                contentView.setOnClickListener {
                    // Hide both the status bar and the navigation bar.
                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                }
            } else {
                contentView.setOnClickListener {
                    // Show both the status bar and the navigation bar.
                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                }
            }
            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }

        setContent {
            HymnalTheme(darkTheme = true, dynamicColor = false) {
                CircuitContent(
                    screen = ImmersiveContentScreen(hymnId),
                    circuit = circuit,
                    modifier = Modifier.fillMaxSize()
                        .clickable {
                            onClickStuff()
                        }
                )
            }
        }
    }

    companion object {
        private const val HYMN_ID = "arg:hymn-id"

        fun launchIntent(
            context: Context,
            hymnId: String
        ): Intent {
            return Intent(context, ImmersiveContentActivity::class.java).apply {
                putExtra(HYMN_ID, hymnId)
            }
        }
    }
}