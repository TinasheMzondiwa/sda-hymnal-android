package hymnal.sing.components.model

import androidx.compose.runtime.Immutable
import hymnal.services.prefs.model.AppFont

@Immutable
data class TextStyleSpec(val font: AppFont = AppFont.POPPINS, val textSize: Float = 18f)
