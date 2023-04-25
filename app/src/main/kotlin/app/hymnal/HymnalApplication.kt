package app.hymnal

import android.app.Application

class HymnalApplication : Application() {

    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.factory().create(this).apply {
            inject(this@HymnalApplication)
        }
    }
}