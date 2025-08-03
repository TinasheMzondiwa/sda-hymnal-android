package app.hymnal

import android.app.Application
import app.hymnal.di.AppGraph
import dev.zacsweers.metro.createGraphFactory
import timber.log.Timber

class HymnalApplication : Application() {

    /** Holder reference for the app graph for [HymnalAppComponentFactory]. */
    val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }
}