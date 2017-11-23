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

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.tinashe.sdah.R


/**
 * Created by tinashe on 2015/08/30.
 */
class PickerDialogFragment : DialogFragment(), View.OnClickListener {

    private var maxHymn = 830

    private var mTextNumDisplay: TextView? = null

    var mCallBack: OnPickerListener? = null

    fun setMaxHymn(maxHymn: Int) {
        this.maxHymn = maxHymn
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val rootView = LayoutInflater.from(
                activity).inflate(R.layout.number_picker_view, null, false)

        mTextNumDisplay = rootView.findViewById(R.id.tvDisplay)

        val l1 = rootView.findViewById<LinearLayout>(R.id.first)
        (l1.findViewById<View>(R.id.key_left) as Button).text = "1"
        (l1.findViewById<View>(R.id.key_middle) as Button).text = "2"
        (l1.findViewById<View>(R.id.key_right) as Button).text = "3"

        l1.findViewById<View>(R.id.key_left).setOnClickListener(this)
        l1.findViewById<View>(R.id.key_middle).setOnClickListener(this)
        l1.findViewById<View>(R.id.key_right).setOnClickListener(this)

        val l2 = rootView.findViewById<LinearLayout>(R.id.second)
        (l2.findViewById<View>(R.id.key_left) as Button).text = "4"
        (l2.findViewById<View>(R.id.key_middle) as Button).text = "5"
        (l2.findViewById<View>(R.id.key_right) as Button).text = "6"

        l2.findViewById<View>(R.id.key_left).setOnClickListener(this)
        l2.findViewById<View>(R.id.key_middle).setOnClickListener(this)
        l2.findViewById<View>(R.id.key_right).setOnClickListener(this)

        val l3 = rootView.findViewById<LinearLayout>(R.id.third)
        (l3.findViewById<View>(R.id.key_left) as Button).text = "7"
        (l3.findViewById<View>(R.id.key_middle) as Button).text = "8"
        (l3.findViewById<View>(R.id.key_right) as Button).text = "9"

        l3.findViewById<View>(R.id.key_left).setOnClickListener(this)
        l3.findViewById<View>(R.id.key_middle).setOnClickListener(this)
        l3.findViewById<View>(R.id.key_right).setOnClickListener(this)

        rootView.findViewById<View>(R.id.btn_zero).setOnClickListener(this)

        rootView.findViewById<View>(R.id.delete).setOnClickListener {

            mTextNumDisplay?.let {
                val curr = it.text.toString()
                if (!TextUtils.isEmpty(curr)) {
                    val size = curr.length

                    if (size == 1)
                        it.text = ""
                    else {
                        val sb = StringBuilder(curr)
                        sb.deleteCharAt(size - 1)
                        it.text = sb.toString()
                    }
                }
            }
        }


        return AlertDialog.Builder(context!!)
                .setView(rootView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    mTextNumDisplay?.let {
                        if (!TextUtils.isEmpty(it.text)) {
                            val hymn = Integer.valueOf(it.text.toString())!!
                            if (hymn > 0) {
                                mCallBack?.let { it.onHymnSelected(hymn) }
                            }
                        }
                    }
                }
                .create()

    }

    override fun onClick(view: View) {
        val num = (view as Button).text

        mTextNumDisplay?.let {
            it.append(num)
            it.text = Integer.valueOf(mTextNumDisplay!!.text.toString())!!.toString() + ""

            if (Integer.parseInt(it.text.toString()) > maxHymn) {
                it.text = maxHymn.toString()
            }
        }

    }

    fun setCallBack(mCallBack: OnPickerListener) {
        this.mCallBack = mCallBack
    }

    interface OnPickerListener {

        fun onHymnSelected(hymn: Int)
    }
}
