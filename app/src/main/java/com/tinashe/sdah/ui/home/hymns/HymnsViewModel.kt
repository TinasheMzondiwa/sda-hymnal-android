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

    var hymnsList: MutableLiveData<List<Hymn>> = MutableLiveData()

    init {
        fetchHymnList()
    }

    private fun fetchHymnList() {
        val disposable = hymnsDao.listAllBooks()
                .subscribeOn(schedulers.database)
                .observeOn(schedulers.main)
                .subscribe({
                    Log.d(javaClass.name, "List: " + it?.toString())
                },
                        {
                            Log.e(javaClass.name, it.message, it)
                        })

        disposables.add(disposable)
    }
}