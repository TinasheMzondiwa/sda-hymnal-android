package hymnal.services.content

import hymnal.services.model.Hymn
import kotlinx.coroutines.flow.Flow

interface HymnalContentProvider {
    fun hymns(): Flow<List<Hymn>>
}