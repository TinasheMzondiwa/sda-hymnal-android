/*
 * Copyright (c) 2017. Tinashe Mzondiwa (www.tinashemzondiwa.co.za)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.tinashe.sdah.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.tinashe.sdah.model.constants.Hymnal
import com.tinashe.sdah.model.constants.UiPref

/**
 * Created by tinashe on 2017/11/10.
 */

class HymnalPrefsImpl constructor(private val context: Context) : HymnalPrefs {

    companion object {
        const val KEY_UI_THEME = "pref_ui_theme"
        const val KEY_LAST_PAGE = "pref_last_opened_page"
        const val KEY_HYMNAL_TYPE = "pref_hymnal_type"
    }

    @UiPref
    override fun getUiPref(): String = getPrefs().getString(KEY_UI_THEME, UiPref.DAY_NIGHT)

    override fun setUiPref(@UiPref pref: String) {
        getPrefs().edit { putString(KEY_UI_THEME, pref) }
    }

    override fun getLastOpenedPage(): Int = getPrefs().getInt(KEY_LAST_PAGE, 0)

    override fun setLastOpenedPage(page: Int) {
        getPrefs().edit { putInt(KEY_LAST_PAGE, page) }
    }

    @Hymnal
    override fun getHymnal(): Int = getPrefs().getInt(KEY_HYMNAL_TYPE, Hymnal.ENGLISH)

    override fun setHymnal(@Hymnal type: Int) {
        getPrefs().edit { putInt(KEY_HYMNAL_TYPE, type) }
    }

    private fun getPrefs(): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

}

/**
 * SharedPreferences.edit extension
 */
inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.func()
    editor.apply()
}