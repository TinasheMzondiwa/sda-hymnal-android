package hymnal.services.content

import hymnal.libraries.model.SabbathResource
import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory
import kotlinx.coroutines.flow.Flow

interface HymnalContentProvider {
    fun hymns(): Flow<List<Hymn>>
    fun categories(): Flow<List<HymnCategory>>
    fun search(query: String): Flow<List<Hymn>>
    fun hymn(index: String): Flow<Hymn?>
    suspend fun hymn(number: Int): Hymn?
    fun sabbathHymns(): Flow<List<Hymn>>
    fun sabbathResources(): Flow<List<SabbathResource>>
}