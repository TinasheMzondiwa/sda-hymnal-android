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

    /**
     * Whether the user has enabled Sabbath reminders.
     */
    fun sabbathRemindersEnabled(): Flow<Boolean>

    suspend fun isSabbathRemindersEnabled(): Boolean

    /**
     * Updates the Sabbath reminders preference.
     * @param enabled True if Sabbath reminders are enabled, false otherwise.
     */
    suspend fun setSabbathRemindersEnabled(enabled: Boolean)

}