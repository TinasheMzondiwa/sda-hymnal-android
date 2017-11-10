package com.tinashe.sdah

import android.app.Activity
import android.app.Application
import com.tinashe.sdah.injection.DaggerHymnalComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Created by tinashe on 2017/11/09.
 */

class HymnalApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)

    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector;
    }

    object AppInjector {

        fun init(app: HymnalApp) {
            DaggerHymnalComponent.builder().application(app).build().inject(app)
        }

    }

}
