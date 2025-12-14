package hymnal.services.content

import hymnal.libraries.model.SabbathResource
import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory
import kotlinx.coroutines.flow.Flow

interface HymnalContentProvider {
    fun hymns(year: String): Flow<List<Hymn>>
    fun categories(year: String): Flow<List<HymnCategory>>
    fun search(query: String): Flow<List<Hymn>>
    fun hymn(index: String): Flow<Hymn?>
    suspend fun hymn(number: Int, year: String): Hymn?
    fun recentHymns(limit: Int = 10): Flow<List<Hymn>>
    fun sabbathHymns(): Flow<List<Hymn>>
    fun sabbathResources(): Flow<List<SabbathResource>>
}