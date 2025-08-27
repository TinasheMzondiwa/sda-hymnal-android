package hymnal.services.prefs.model

import androidx.annotation.StringRes
import hymnal.libraries.l10n.R as L10nR

enum class AppTheme(@param:StringRes val label: Int) {
    FOLLOW_SYSTEM(label = L10nR.string.pref_theme_follow_system),
    LIGHT(label = L10nR.string.pref_theme_light),
    DARK(label = L10nR.string.pref_theme_dark);

    companion object {
        fun fromName(name: String?): AppTheme = name?.let {
            entries.associateBy(AppTheme::name)[it] ?: FOLLOW_SYSTEM
        } ?: FOLLOW_SYSTEM
    }
}