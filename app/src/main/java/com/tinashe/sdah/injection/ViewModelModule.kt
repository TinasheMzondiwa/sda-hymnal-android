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

package com.tinashe.sdah.injection


import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import com.tinashe.sdah.ui.home.HomeViewModel
import com.tinashe.sdah.ui.home.featured.FeaturedViewModel
import com.tinashe.sdah.ui.home.hymns.HymnsViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HymnsViewModel::class)
    internal abstract fun bindHymnsViewModel(hymnsViewModel: HymnsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FeaturedViewModel::class)
    internal abstract fun bindFeaturedViewModel(featuredViewModel: FeaturedViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFractory(factory: ViewModelFactory): ViewModelProvider.Factory
}
