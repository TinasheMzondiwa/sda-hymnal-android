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

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.tinashe.sdah.db.dao.HymnsDao
import com.tinashe.sdah.model.Hymn
import com.tinashe.sdah.prefs.HymnalPrefs
import com.tinashe.sdah.ui.base.RxAwareViewModel
import com.tinashe.sdah.util.RxSchedulers
import javax.inject.Inject

/**
 * Created by tinashe on 2017/11/14.
 */
class HymnsViewModel
@Inject constructor(private val prefs: HymnalPrefs,
                    private val schedulers: RxSchedulers,
                    private val hymnsDao: HymnsDao) : RxAwareViewModel() {

    val hymnsList: MutableLiveData<List<Hymn>> = MutableLiveData()
    val currentPage: MutableLiveData<Int> = MutableLiveData()

    init {
        fetchHymnList()
    }

    private fun fetchHymnList() {
        val disposable = hymnsDao.findByType(prefs.getHymnal())
                .subscribeOn(schedulers.database)
                .observeOn(schedulers.main)
                .subscribe({
                    it?.let {
                        hymnsList.value = it.hymns

                        currentPage.value = prefs.getLastOpenedPage()
                    }

                }, { Log.e(javaClass.name, it.message, it) })

        disposables.add(disposable)
    }

    fun hymnSelected(number: Int) {
        val page = number - 1
        prefs.setLastOpenedPage(page)
        currentPage.value = page
    }

    fun searchHymns(query: String): MutableList<Hymn> {

        val items = hymnsList.value?.filter { it.content?.contains(query, true) ?: false }

        return items?.toMutableList() ?: mutableListOf()
    }
}