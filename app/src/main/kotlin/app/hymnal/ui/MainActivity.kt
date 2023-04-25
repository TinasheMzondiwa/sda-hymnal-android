package app.hymnal.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import com.slack.circuit.foundation.CircuitConfig
import com.squareup.anvil.annotations.ContributesMultibinding
import hymnal.di.ActivityKey
import hymnal.di.AppScope
import javax.inject.Inject

@ActivityKey(MainActivity::class)
@ContributesMultibinding(AppScope::class, boundType = Activity::class)
class MainActivity
@Inject constructor(
    private val circuitConfig: CircuitConfig
) : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            HymnalApp(
                circuitConfig,
                calculateWindowSizeClass(this).widthSizeClass
            )
        }
    }
}

