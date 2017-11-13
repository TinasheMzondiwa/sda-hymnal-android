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

package com.tinashe.sdah.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.tinashe.sdah.R
import com.tinashe.sdah.model.constants.UiPref.*
import com.tinashe.sdah.prefs.HymnalPrefs
import com.tinashe.sdah.ui.home.HomeActivity
import javax.inject.Inject

/**
 * Created by tinashe on 2017/11/10.
 */

abstract class BaseThemedActivity : AppCompatActivity() {

    @Inject protected lateinit var preferences: HymnalPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (preferences.getUiPref()) {
            DAY -> delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            NIGHT -> delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            DAY_NIGHT -> delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        }

        if (this is HomeActivity) {
            setTheme(R.style.Hymnal_Theme_Main_Green)
        }

    }
}