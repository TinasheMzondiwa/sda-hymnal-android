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
import hymnal.libraries.model.Hymnal
import hymnal.services.prefs.HymnalPrefs
import hymnal.services.prefs.model.AppFont
import hymnal.services.prefs.model.AppTheme
import hymnal.services.prefs.model.ThemeStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hymnal_settings")

@ContributesBinding(scope = AppScope::class)
@Inject
class HymnalPrefsImpl(
    context: Context,
    private val dispatcherProvider: DispatcherProvider
) : HymnalPrefs {

    private val dataStore: DataStore<Preferences> by lazy { context.dataStore }

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

    private val appTheme: Flow<AppTheme> = dataStore.data.map { preferences ->
        AppTheme.fromName(preferences[PreferenceKeys.APP_THEME])
    }

    private val textSize: Flow<Float> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.FONT_SIZE] ?: DEFAULT_FONT_SIZE
    }
    private val typeface: Flow<AppFont> = dataStore.data.map { preferences ->
        AppFont.fromName(preferences[PreferenceKeys.TYPEFACE_NAME])
    }

    private val dynamicColors: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DYNAMIC_COLORS] ?: false
    }

    private suspend fun updateAppTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.APP_THEME] = theme.name
        }
    }

    private suspend fun updateFontSize(newSize: Float) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.FONT_SIZE] = newSize
        }
    }

    private suspend fun updateTypeface(newTypefaceName: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TYPEFACE_NAME] = newTypefaceName
        }
    }

    private suspend fun updateDynamicColors(dynamicColors: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.DYNAMIC_COLORS] = dynamicColors
        }
    }

    override fun sabbathRemindersEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferenceKeys.SABBATH_REMINDER] ?: false
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun isSabbathRemindersEnabled(): Boolean {
        return withContext(dispatcherProvider.io) {
            val preferences = dataStore.data.first()
            preferences[PreferenceKeys.SABBATH_REMINDER] ?: false
        }
    }

    override suspend fun setSabbathRemindersEnabled(enabled: Boolean): Unit =
        withContext(dispatcherProvider.io) {
            dataStore.edit { preferences ->
            preferences[PreferenceKeys.SABBATH_REMINDER] = enabled
        }
    }

    override fun currentHymnal(): Flow<Hymnal> {
        return dataStore.data.map { preferences ->
            val year = preferences[PreferenceKeys.CURRENT_HYMNAL] ?: Hymnal.NewHymnal.year
            Hymnal.fromYear(year) ?: Hymnal.NewHymnal
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun updateCurrentHymnal(hymnal: Hymnal): Unit =
        withContext(dispatcherProvider.io) {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.CURRENT_HYMNAL] = hymnal.year
            }
        }

    override fun showLastHymn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferenceKeys.SHOW_LAST_HYMN] ?: false
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun setShowLastHymn(enable: Boolean): Unit =
        withContext(dispatcherProvider.io) {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.SHOW_LAST_HYMN] = enable
            }
        }

    override fun lastHymnIndex(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PreferenceKeys.LAST_HYMN_INDEX]
        }
    }

    override suspend fun setLastHymnIndex(index: String): Unit = withContext(dispatcherProvider.io) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_HYMN_INDEX] = index
        }
    }

    // Define preference keys
    private object PreferenceKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val FONT_SIZE = floatPreferencesKey("font_size")
        val TYPEFACE_NAME = stringPreferencesKey("typeface_name")
        val SABBATH_REMINDER = booleanPreferencesKey("sabbath_reminders_enabled")
        val CURRENT_HYMNAL = stringPreferencesKey("current_hymnal")
        val SHOW_LAST_HYMN = booleanPreferencesKey("show_last_hymn_card")
        val LAST_HYMN_INDEX = stringPreferencesKey("last_hymn_index")
    }

    // Default values
    companion object {
        private const val DEFAULT_FONT_SIZE = 18f // Example default font size
    }

}