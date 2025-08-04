package hymnal.services.content

import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory
import kotlinx.coroutines.flow.Flow

interface HymnalContentProvider {
    fun hymns(): Flow<List<Hymn>>
    fun categories(): Flow<List<HymnCategory>>
}