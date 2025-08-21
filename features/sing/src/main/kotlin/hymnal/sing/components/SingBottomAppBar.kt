package hymnal.sing.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.ui.extensions.plus
import hymnal.ui.theme.HymnalTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SingBottomAppBar(
    number: Int,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    scrollBehavior: BottomAppBarScrollBehavior? = null,
    previousEnabled: Boolean = true,
    nextEnabled: Boolean = true,
    onPlayPause: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {},
    onGoToHymn: () -> Unit = {},
) {
    val iconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
    val textButtonColors = ButtonDefaults.textButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
    val layoutDirection = LocalLayoutDirection.current
    val contentPadding = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal
    ).asPaddingValues().plus(layoutDirection, start = 16.dp, end = 16.dp)

    FlexibleBottomAppBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        scrollBehavior = scrollBehavior,
    ) {
        IconButton(
            onClick = onPlayPause,
            enabled = previousEnabled,
            colors = iconButtonColors,
        ) {
            AnimatedContent(
                targetState = isPlaying,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "playPauseIconAnimation"
            ) { targetIsPlaying ->
                Icon(
                    imageVector = if (targetIsPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "Play/Pause Icon",
                    modifier = Modifier
                )
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(48.dp)
                )
        ) {
            IconButton(onClick = onPrevious, colors = iconButtonColors) {
                Icon(Icons.Rounded.ChevronLeft, null)
            }

            TextButton(
                onClick = onGoToHymn,
                modifier = Modifier.weight(1f),
                colors = textButtonColors,
            ) {
                Text("Hymn $number", style = MaterialTheme.typography.titleMedium)
            }

            IconButton(
                onClick = onNext,
                enabled = nextEnabled,
                colors = iconButtonColors,
            ) {
                Icon(Icons.Rounded.ChevronRight, null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            SingBottomAppBar(334)
        }
    }
}