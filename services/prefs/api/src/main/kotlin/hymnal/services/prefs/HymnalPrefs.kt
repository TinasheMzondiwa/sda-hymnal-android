package hymnal.services.prefs

import hymnal.services.prefs.model.ThemeStyle
import kotlinx.coroutines.flow.Flow

interface HymnalPrefs {

    /**
     * Emits the current theme style for the hymnal content.
     */
    fun themeStyle(): Flow<ThemeStyle>

    /**
     * Updates the theme style preference.
     * @param theme The new theme style to store.
     */
    suspend fun updateThemeStyle(theme: ThemeStyle)
}