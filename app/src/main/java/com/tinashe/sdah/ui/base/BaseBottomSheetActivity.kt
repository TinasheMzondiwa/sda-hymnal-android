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
import android.support.design.widget.BottomSheetBehavior
import android.view.View

/**
 * Created by tinashe on 2017/11/14.
 */
abstract class BaseBottomSheetActivity : BaseThemedActivity() {

    protected abstract fun layoutRes(): Int

    protected var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {

            when (newState) {
                BottomSheetBehavior.STATE_DRAGGING -> {
                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                }
                BottomSheetBehavior.STATE_HIDDEN -> finish()
            }

        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes())

        /*val layoutParams = bottomSheet.getLayoutParams() as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior

        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            bottomSheetBehavior = behavior
            bottomSheetBehavior!!.setBottomSheetCallback(mBottomSheetBehaviorCallback)
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        }*/
    }
}