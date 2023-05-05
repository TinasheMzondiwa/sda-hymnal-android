package app.hymnal

import android.app.Application
import app.hymnal.di.ApplicationComponent
import app.hymnal.di.DaggerApplicationComponent

class HymnalApplication : Application() {

    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.factory().create(this).apply {
            inject(this@HymnalApplication)
        }
    }
}