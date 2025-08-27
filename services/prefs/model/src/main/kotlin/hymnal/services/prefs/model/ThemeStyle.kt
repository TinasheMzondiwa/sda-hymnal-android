package hymnal.services.prefs.model

import androidx.annotation.Keep

@Keep
data class ThemeStyle(
    val theme: AppTheme = AppTheme.FOLLOW_SYSTEM,
    val font: AppFont = AppFont.POPPINS,
    val textSize: Float = 18f,
)
