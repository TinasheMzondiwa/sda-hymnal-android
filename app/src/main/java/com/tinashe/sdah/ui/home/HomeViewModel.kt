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

package com.tinashe.sdah.ui.home

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.tinashe.sdah.BuildConfig
import com.tinashe.sdah.retrofit.UnSplashApi
import com.tinashe.sdah.ui.base.RxAwareViewModel
import com.tinashe.sdah.util.RxSchedulers
import javax.inject.Inject

/**
 * Created by tinashe on 2017/11/12.
 */

class HomeViewModel
@Inject constructor(private val schedulers: RxSchedulers,
                    private val unSplashApi: UnSplashApi) : RxAwareViewModel() {

    var urlData: MutableLiveData<String> = MutableLiveData()

    init {
        fetchBackdrop()
    }

    private fun fetchBackdrop() {

        val disposable = unSplashApi.getBackdrop(BuildConfig.UNSPLASH_API_SECRET, "nature, sunset")
                .subscribeOn(schedulers.network)
                .observeOn(schedulers.main)
                .subscribe({
                    if (it.isSuccessful) {
                        val backdrop = it.body()

                        val url = backdrop?.urls?.regular
                        urlData.value = url.orEmpty()

                    }
                }, {
                    Log.e(javaClass.name, it.message, it)
                })

        disposables.add(disposable)

    }
}
