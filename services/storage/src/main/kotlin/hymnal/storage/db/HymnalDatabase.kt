package hymnal.storage.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.dao.SectionsDao
import hymnal.storage.db.entity.HymnEntity
import hymnal.storage.db.entity.SectionEntity

@Database(
    entities = [HymnEntity::class, SectionEntity::class],
    version = 1,
    exportSchema = true,
)
internal abstract class HymnalDatabase : RoomDatabase() {

    abstract fun hymnsDao(): HymnsDao

    abstract fun sectionsDao(): SectionsDao

    companion object {
        private const val DATABASE_NAME = "hymnal.db"

        @Volatile
        private var INSTANCE: HymnalDatabase? = null

        fun getInstance(context: Context): HymnalDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): HymnalDatabase =
            Room.databaseBuilder(context, HymnalDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}