package com.tinashe.sdah.injection

import com.tinashe.sdah.HymnalApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by tinashe on 2017/11/09.
 */
@Singleton
@Component(modules = arrayOf(
        HymnalAppModule::class,
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        ViewModelModule::class,
        ActivityBuilder::class))
interface HymnalComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: HymnalApp): Builder

        fun build(): HymnalComponent
    }

    fun inject(app: HymnalApp)
}
