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

package com.tinashe.sdah.ui.home.favorites

import com.tinashe.sdah.R
import com.tinashe.sdah.ui.base.BaseDrawerFragment

/**
 * Created by tinashe on 2017/11/14.
 */
class FavoritesFragment : BaseDrawerFragment() {
    override fun titleRes(): Int {
        return R.string.menu_favorites
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_hymns
    }
}