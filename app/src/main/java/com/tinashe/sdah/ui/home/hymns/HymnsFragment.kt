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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import com.tinashe.sdah.R
import com.tinashe.sdah.ui.base.BaseDrawerFragment
import com.tinashe.sdah.util.AnimUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_hymns.*
import javax.inject.Inject

/**
 * Created by tinashe on 2017/11/14.
 */
class HymnsFragment : BaseDrawerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HymnsViewModel

    override fun titleRes(): Int {
        return R.string.app_name
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidSupportInjection.inject(this)

        pager.pageMargin = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        pager.setPageMarginDrawable(R.drawable.page_margin)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(HymnsViewModel::class.java)

        viewModel.hymnsList.observe(this, Observer { })
    }

    fun fabClicked(fab: FloatingActionButton) {
        val fragment = FabMenuFragment()
        fragment.point = AnimUtil.getCenterForView(fab)
        fragment.show(childFragmentManager, fragment.tag)
    }
}