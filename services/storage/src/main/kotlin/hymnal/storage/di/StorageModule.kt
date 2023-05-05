package hymnal.storage.di

import android.content.Context
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import hymnal.di.AppScope
import hymnal.di.SingleIn
import hymnal.di.qualifiers.ApplicationContext
import hymnal.storage.db.HymnalDatabase
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.dao.SectionsDao

@ContributesTo(AppScope::class)
@Module
object StorageModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideDatabase(
        @ApplicationContext context: Context
    ): HymnalDatabase = HymnalDatabase.getInstance(context)

    @Provides
    @SingleIn(AppScope::class)
    fun provideHymnsDao(database: HymnalDatabase): HymnsDao = database.hymnsDao()

    @Provides
    @SingleIn(AppScope::class)
    fun provideSectionsDao(database: HymnalDatabase): SectionsDao = database.sectionsDao()

}