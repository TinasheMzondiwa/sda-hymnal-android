package app.hymnal.di

import android.app.Activity
import android.app.Application
import app.hymnal.HymnalApplication
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import hymnal.di.AppScope
import hymnal.di.SingleIn
import hymnal.storage.di.StorageModule
import javax.inject.Provider

@MergeComponent(
    scope = AppScope::class,
    modules = [
        StorageModule::class,
    ]
)
@SingleIn(AppScope::class)
interface ApplicationComponent {

    val activityProviders: Map<Class<out Activity>, @JvmSuppressWildcards Provider<Activity>>

    fun inject(application: HymnalApplication)

    @Component.Factory
    fun interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}