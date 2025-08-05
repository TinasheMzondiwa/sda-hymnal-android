package hymnal.ui.widget.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.size.Scale
import hymnal.ui.widget.content.ContentSlot

/**
 * A remote image [ContentSlot]
 *
 * Sample usage:
 * ```kotlin
 *      ContentBox(
 *          content = RemoteImage(
 *              data = "url",
 *              contentDescription = "description",
 *              loading = { },
 *              error = { }
 *          ),
 *          modifier = Modifier.size(40.dp)
 *      )
 * ```
 */
@Immutable
data class RemoteImage(
    val data: String?,
    val contentDescription: String? = null,
    val contentScale: ContentScale = ContentScale.Crop,
    val scale: Scale = Scale.FIT,
    val colorFilter: ColorFilter? = null,
    val loading: @Composable () -> Unit = {},
    val error: @Composable () -> Unit = {},
) : ContentSlot {

    @Composable
    override fun Content() {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data)
                .crossfade(true)
                .scale(scale = scale)
                .allowHardware(false)
                .build()
        )

        val state by painter.state.collectAsState()
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                loading()
            }

            AsyncImagePainter.State.Empty,
            is AsyncImagePainter.State.Error -> {
                error()
            }

            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    colorFilter = colorFilter
                )
            }
        }
    }
}
