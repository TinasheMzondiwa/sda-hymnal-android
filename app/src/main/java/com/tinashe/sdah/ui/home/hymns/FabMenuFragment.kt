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

import android.graphics.Point
import android.widget.ImageButton
import com.tinashe.sdah.R
import com.tinashe.sdah.ui.base.BaseBottomSheetDialogFragment

/**
 * Created by tinashe on 2017/11/14.
 */
class FabMenuFragment : BaseBottomSheetDialogFragment() {

    var point: Point? = null

    override fun layoutRes(): Int = R.layout.fragment_fab_menu

    override fun initialize() {

        val btn1 = rootView?.findViewById<ImageButton>(R.id.imageButton)
        val btn2 = rootView?.findViewById<ImageButton>(R.id.imageButton2)
        val btn3 = rootView?.findViewById<ImageButton>(R.id.imageButton3)
        val btn4 = rootView?.findViewById<ImageButton>(R.id.imageButton4)
        val btn5 = rootView?.findViewById<ImageButton>(R.id.imageButton5)
        val btn6 = rootView?.findViewById<ImageButton>(R.id.imageButton6)
        val btn7 = rootView?.findViewById<ImageButton>(R.id.imageButton7)
        val btn8 = rootView?.findViewById<ImageButton>(R.id.imageButton8)

    }
}