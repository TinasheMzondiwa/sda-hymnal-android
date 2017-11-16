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

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.tinashe.sdah.model.FeaturedVideo
import com.tinashe.sdah.ui.base.RxAwareViewModel
import com.tinashe.sdah.util.RxFirebase
import com.tinashe.sdah.util.RxSchedulers
import javax.inject.Inject

/**
 * Created by tinashe on 2017/11/15.
 */
class FeaturedViewModel @Inject constructor(private val schedulers: RxSchedulers,
                                            private val database: FirebaseDatabase) : RxAwareViewModel() {

    companion object {
        const val FEATURED: String = "featuredVideos"
    }

    var featuredVideoList: MutableLiveData<List<FeaturedVideo>> = MutableLiveData()

    init {
        fetchList()
    }

    private fun fetchList() {

        val disposable = RxFirebase.observe(database.getReference(FEATURED))
                .subscribeOn(schedulers.database)
                .observeOn(schedulers.main)
                .subscribe({

                    if (it.childrenCount > 0) {

                        //Add all videos that are not suggestions
                        featuredVideoList.value = it.children.map { it.getValue(FeaturedVideo::class.java)!! }
                                .filter { featuredVideo -> !featuredVideo.isSuggestion }

                    }

                }, { Log.e(javaClass.name, it.message, it) })

        disposables.add(disposable)
    }
}