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


import android.annotation.SuppressLint
import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.View

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }

        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    protected abstract fun layoutRes(): Int

    protected abstract fun initialize()

    protected var rootView: View? = null


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        rootView = View.inflate(context, layoutRes(), null)
        dialog.setContentView(rootView)
        val layoutParams = (rootView?.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {

            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }

        initialize()
    }
}
