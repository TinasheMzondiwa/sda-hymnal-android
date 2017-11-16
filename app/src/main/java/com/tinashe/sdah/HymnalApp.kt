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

package com.tinashe.sdah

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.database.FirebaseDatabase
import com.tinashe.sdah.injection.DaggerHymnalComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

/**
 * Created by tinashe on 2017/11/09.
 */

class HymnalApp : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)

    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    object AppInjector {

        fun init(app: HymnalApp) {

            //Init Crashlytics
            val crashlyticsCore = CrashlyticsCore.Builder()
                    .disabled(BuildConfig.DEBUG)
                    .build()
            val fabric = Fabric.Builder(app)
                    .kits(Answers(), Crashlytics.Builder().core(crashlyticsCore).build())
                    .debuggable(true)
                    .build()
            Fabric.with(fabric)

            //Init Dagger
            DaggerHymnalComponent.builder().application(app).build().inject(app)

            //Init Firebase
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }

    }

}
