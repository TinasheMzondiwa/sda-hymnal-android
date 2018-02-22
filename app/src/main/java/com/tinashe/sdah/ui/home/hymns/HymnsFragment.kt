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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.tinashe.sdah.R
import com.tinashe.sdah.model.Hymn
import com.tinashe.sdah.ui.base.BaseDrawerFragment
import com.tinashe.sdah.ui.custom.UniversalAdapter
import com.tinashe.sdah.ui.custom.extensions.circularConceal
import com.tinashe.sdah.ui.custom.extensions.circularReveal
import com.tinashe.sdah.ui.custom.extensions.getCenter
import com.tinashe.sdah.ui.custom.extensions.hide
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

    private lateinit var pagerAdapter: HymnsPagerAdapter

    private lateinit var searchResultsAdapter: UniversalAdapter<Hymn, SearchResultHolder>

    private lateinit var searchIcon: MenuItem

    override fun titleRes(): Int = R.string.app_name

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_hymns, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidSupportInjection.inject(this)

        pager.pageMargin = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        pager.setPageMarginDrawable(R.drawable.page_margin)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(HymnsViewModel::class.java)

        viewModel.hymnsList.observe(this, Observer {
            it?.let {
                pagerAdapter = HymnsPagerAdapter(it)
                pager.adapter = pagerAdapter
            }
        })

        viewModel.currentPage.observe(this, Observer { it?.let { pager.currentItem = it } })

        searchResultsList.layoutManager = LinearLayoutManager(activity)
        searchResultsAdapter = UniversalAdapter(
                { parent, _ -> SearchResultHolder.inflate(parent) },
                { vh, _, hymn ->
                    vh.bind(hymn, {
                        searchIcon.collapseActionView()
                        pager.postDelayed({ pager.currentItem = hymn.number - 1 }, 400)
                    })
                }
        )
        searchResultsList.adapter = searchResultsAdapter
    }

    override fun fabClicked(fab: FloatingActionButton) {
        searchResultsList.hide()

        val fragment = FabMenuFragment()
        fragment.point = fab.getCenter()
        fragment.options = object : FabMenuFragment.HymnOptions {
            override fun pickHymn() {
                val picker = PickerDialogFragment()
                picker.mCallBack = object : PickerDialogFragment.OnPickerListener {
                    override fun onHymnSelected(hymn: Int) {
                        viewModel.hymnSelected(hymn)
                    }
                }
                picker.show(childFragmentManager, picker.tag)
            }
        }
        fragment.show(childFragmentManager, fragment.tag)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_hymns, menu)

        searchIcon = menu.findItem(R.id.search)
        val searchView = searchIcon.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val items = viewModel.searchHymns(it)
                    searchResultsAdapter.items = items
                }
                return true
            }
        })
        searchIcon.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                searchResultsList.circularReveal(searchPoint.getCenter())
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                searchResultsList.circularConceal(searchPoint.getCenter())
                return true
            }

        })
    }

    override fun onStop() {
        pager?.let { viewModel.hymnSelected(it.currentItem + 1) }
        super.onStop()
    }
}