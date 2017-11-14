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
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import com.tinashe.sdah.BuildConfig
import com.tinashe.sdah.retrofit.UnSplashApi
import com.tinashe.sdah.ui.base.RxAwareViewModel
import com.tinashe.sdah.util.RxSchedulers
import java.util.*
import javax.inject.Inject

/**
 * Created by tinashe on 2017/11/12.
 */

class HomeViewModel
@Inject constructor(private val schedulers: RxSchedulers,
                    private val unSplashApi: UnSplashApi) : RxAwareViewModel() {

    var navigation: MutableLiveData<Int> = MutableLiveData()
    var urlData: MutableLiveData<String> = MutableLiveData()
    val sabbathDate: MutableLiveData<Calendar> = MutableLiveData()

    init {
        navigation.value = Navigation.HYMNS

        fetchBackdrop()
    }

    fun navigationSelected(selectedId: Int) {
        navigation.value = selectedId
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

    fun calculateSunset(latitude: Double, longitude: Double) {
        val calculator = SunriseSunsetCalculator(Location(latitude, longitude), TimeZone.getDefault())
        val nextSabbath = Calendar.getInstance(TimeZone.getDefault())
        nextSabbath.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)

        val officialSunset = calculator.getOfficialSunsetCalendarForDate(nextSabbath)
        sabbathDate.value = officialSunset
    }
}
