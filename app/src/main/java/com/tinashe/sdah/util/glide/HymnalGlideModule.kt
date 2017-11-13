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

package com.tinashe.sdah.util.glide

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888
import com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class HymnalGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val defaultOptions = RequestOptions()
        // Prefer higher quality images unless we're on a low RAM device
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager

        defaultOptions.format(if (activityManager.isLowRamDevice) PREFER_RGB_565 else PREFER_ARGB_8888)

        // Disable hardware bitmaps as they don't play nicely with Palette
        defaultOptions.disallowHardwareConfig()
        builder.setDefaultRequestOptions(defaultOptions)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
