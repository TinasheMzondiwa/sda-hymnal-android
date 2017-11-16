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

package com.tinashe.sdah.ui.home.featured

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tinashe.sdah.R
import com.tinashe.sdah.ui.base.BaseDrawerFragment
import com.tinashe.sdah.ui.custom.extensions.hide
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_featured.*
import javax.inject.Inject

/**
 * Created by tinashe on 2017/11/14.
 */
class FeaturedFragment : BaseDrawerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: FeaturedViewModel

    private var adapter: FeaturedVideosListAdapter = FeaturedVideosListAdapter()

    override fun titleRes(): Int = R.string.menu_featured

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_featured, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidSupportInjection.inject(this)

        featuredList.layoutManager = LinearLayoutManager(activity)
        featuredList.adapter = adapter

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(FeaturedViewModel::class.java)

        viewModel.featuredVideoList.observe(this, Observer {

            progressBar.hide()

            adapter.videosList = it!!
        })
    }

    override fun fabClicked(fab: FloatingActionButton) {
        //TODO
    }
}