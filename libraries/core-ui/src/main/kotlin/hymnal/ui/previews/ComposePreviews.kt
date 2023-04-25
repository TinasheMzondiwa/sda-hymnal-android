package hymnal.ui.previews

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multi-preview annotation that represents various device sizes.
 */
@Preview(name = "phone", device = Devices.PHONE)
@Preview(name = "foldable", device = Devices.FOLDABLE)
@Preview(name = "tablet", device = Devices.TABLET)
annotation class DevicePreviews


/**
 * Multi-preview annotation that represents Light and Dark mode themes.
 */
@Preview(
    name = "phone ~ light",
    device = Devices.PHONE,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    name = "phone ~ dark",
    device = Devices.PHONE,
    uiMode = UI_MODE_NIGHT_YES
)
annotation class DayNightPreviews
