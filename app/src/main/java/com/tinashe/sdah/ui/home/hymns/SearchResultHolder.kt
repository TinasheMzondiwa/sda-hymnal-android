/*
 * Copyright (c) 2018. Tinashe Mzondiwa (www.tinashemzondiwa.co.za)
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

package com.tinashe.sdah.ui.home.hymns

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tinashe.sdah.R
import com.tinashe.sdah.model.Hymn
import com.tinashe.sdah.ui.custom.extensions.inflateView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_result.*

/**
 * Created by tinashe on 2018/02/22.
 */
class SearchResultHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    companion object {
        fun inflate(parent: ViewGroup):
                SearchResultHolder = SearchResultHolder(inflateView(R.layout.search_result, parent, false))
    }

    fun bind(hymn: Hymn, itemClick: () -> Unit) {
        title.text = hymn.title

        itemView.setOnClickListener { itemClick.invoke() }
    }
}