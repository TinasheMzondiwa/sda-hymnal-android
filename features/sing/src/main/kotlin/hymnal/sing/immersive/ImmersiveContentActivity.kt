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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavEvent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.ui.theme.HymnalTheme
import kotlinx.coroutines.flow.MutableStateFlow
import libraries.hymnal.di.ActivityKey

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(ImmersiveContentActivity::class)
@Inject
class ImmersiveContentActivity(
    private val circuit: Circuit
) : ComponentActivity() {

    private val controlsVisible = MutableStateFlow(true)

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

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())
            ) {
                contentView.setOnClickListener {
                    // Hide both the status bar and the navigation bar.
                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                    controlsVisible.tryEmit(false)
                }
            } else {
                contentView.setOnClickListener {
                    // Show both the status bar and the navigation bar.
                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                    controlsVisible.tryEmit(true)
                }
            }
            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }

        setContent {
            val controlsVisible by controlsVisible.collectAsStateWithLifecycle(initialValue = true)

            HymnalTheme(darkTheme = true, dynamicColor = false) {
                CircuitContent(
                    screen = ImmersiveContentScreen(
                        hymnId = hymnId,
                        showControls = controlsVisible
                    ),
                    circuit = circuit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onClickStuff()
                        },
                    onNavEvent = { navEvent ->
                        when (navEvent) {
                            is NavEvent.Pop -> onBackPressedDispatcher.onBackPressed()
                            else -> Unit
                        }
                    },
                    key = hymnId,
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