package com.tinashe.sdah;

import android.app.Activity;
import android.app.Application;

import com.tinashe.sdah.injection.DaggerHymnalComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Created by tinashe on 2017/11/09.
 */

public class HymnalApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        //Init Dagger
        DaggerHymnalComponent.builder()
                .application(this)
                .build();
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }
}
