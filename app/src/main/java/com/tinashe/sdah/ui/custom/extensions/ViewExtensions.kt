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

package com.tinashe.sdah.ui.custom.extensions

import android.graphics.Point
import android.support.annotation.LayoutRes
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tinashe.sdah.util.VersionUtils
import com.tinashe.sdah.util.glide.GlideApp

/**
 * Created by tinashe on 2017/11/15.
 */

fun inflateView(@LayoutRes layoutResId: Int, parent: ViewGroup, attachToRoot: Boolean): View =
        LayoutInflater.from(parent.context).inflate(layoutResId, parent, attachToRoot)

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun ImageView.loadFromUrl(imageUrl: String?, error: Int) {
    GlideApp.with(this).load(imageUrl).error(error)
            .transition(DrawableTransitionOptions.withCrossFade()).into(this)
}

fun ImageView.loadAvatar(imageUrl: String?, error: Int) {
    GlideApp.with(this).load(imageUrl).placeholder(error).error(error).circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade()).into(this)
}

fun View.getCenter(): Point {
    val centerX = (left + right) / 2
    val centerY = (top + bottom) / 2
    return Point(centerX, centerY)
}

fun TextView.renderHtml(string: String) {
    if (VersionUtils.isAtLeastN()) {
        text = Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
    } else {
        text = Html.fromHtml(string)
    }
}