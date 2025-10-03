package hymnal.services.prefs.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.services.prefs.HymnalPrefs
import hymnal.services.prefs.model.AppFont
import hymnal.services.prefs.model.AppTheme
import hymnal.services.prefs.model.ThemeStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hymnal_settings")

@ContributesBinding(scope = AppScope::class)
@Inject
class HymnalPrefsImpl(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) : HymnalPrefs {

    override fun themeStyle(): Flow<ThemeStyle> =
        combine(
            appTheme,
            textSize,
            typeface,
            dynamicColors,
        ) { appTheme, textSize, typeface, dynamicColors ->
            ThemeStyle(
                theme = appTheme,
                font = typeface,
                textSize = textSize,
                dynamicColors = dynamicColors,
            )
        }.flowOn(dispatcherProvider.io)

    override suspend fun updateThemeStyle(theme: ThemeStyle) {
        withContext(dispatcherProvider.io) {
            updateAppTheme(theme.theme)
            updateTypeface(theme.font.name)
            updateFontSize(theme.textSize)
            updateDynamicColors(theme.dynamicColors)
        }
    }

    private val appTheme: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        AppTheme.fromName(preferences[PreferenceKeys.APP_THEME])
    }

    private val textSize: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.FONT_SIZE] ?: DEFAULT_FONT_SIZE
    }
    private val typeface: Flow<AppFont> = context.dataStore.data.map { preferences ->
        AppFont.fromName(preferences[PreferenceKeys.TYPEFACE_NAME])
    }

    private val dynamicColors: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DYNAMIC_COLORS] ?: false
    }

    private suspend fun updateAppTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.APP_THEME] = theme.name
        }
    }

    private suspend fun updateFontSize(newSize: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.FONT_SIZE] = newSize
        }
    }

    private suspend fun updateTypeface(newTypefaceName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.TYPEFACE_NAME] = newTypefaceName
        }
    }

    private suspend fun updateDynamicColors(dynamicColors: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DYNAMIC_COLORS] = dynamicColors
        }
    }

    override fun sabbathRemindersEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.SABBATH_REMINDER] ?: false
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun setSabbathRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SABBATH_REMINDER] = enabled
        }
    }

    // Define preference keys
    private object PreferenceKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val FONT_SIZE = floatPreferencesKey("font_size")
        val TYPEFACE_NAME = stringPreferencesKey("typeface_name")
        val SABBATH_REMINDER = booleanPreferencesKey("sabbath_reminders_enabled")
    }

    // Default values
    companion object {
        private const val DEFAULT_FONT_SIZE = 18f // Example default font size
    }

}