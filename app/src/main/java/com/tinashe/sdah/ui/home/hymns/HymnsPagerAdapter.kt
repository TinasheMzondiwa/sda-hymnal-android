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

package com.tinashe.sdah.ui.home.hymns

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tinashe.sdah.R
import com.tinashe.sdah.model.Hymn
import com.tinashe.sdah.ui.custom.extensions.inflateView
import com.tinashe.sdah.ui.custom.extensions.renderHtml

/**
 * Created by tinashe on 2017/11/16.
 */
class HymnsPagerAdapter constructor(private var hymnsList: List<Hymn>) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object` as View

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }

    override fun getCount(): Int = hymnsList.size

    fun getHymn(position: Int): Hymn? = hymnsList[position]

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val rootView = inflateView(R.layout.hymn_view, container, false)
        val textView = rootView.findViewById<TextView>(R.id.hymnContent)

        val hymn = hymnsList[position]

        if (hymn.edited != null) {
            textView.renderHtml(hymn.edited!!)
        } else {
            hymn.content?.let { textView.renderHtml(it) }
        }

        rootView.translationX = (-1 * rootView.width * position).toFloat()

        container.addView(rootView, 0)

        return rootView
    }
}