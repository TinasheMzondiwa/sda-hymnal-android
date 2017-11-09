package com.tinashe.sdah.injection;

import android.app.Application;

import com.tinashe.sdah.HymnalApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by tinashe on 2017/11/09.
 */
@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class,
        HymnalAppModule.class,
        ActivityBuilder.class,
})
public interface HymnalComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        HymnalComponent build();
    }

    void inject(HymnalApp application);
}
