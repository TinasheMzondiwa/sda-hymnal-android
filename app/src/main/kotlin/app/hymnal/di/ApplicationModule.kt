package app.hymnal.di

import android.app.Application
import android.content.Context
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import hymnal.di.AppScope
import hymnal.di.SingleIn
import hymnal.di.qualifiers.ApplicationContext

@ContributesTo(AppScope::class)
@Module
abstract class ApplicationModule {

    @Binds
    @ApplicationContext
    @SingleIn(AppScope::class)
    abstract fun Application.provideApplicationContext(): Context

}