package hymnal.content

import hymnal.content.model.IndexSection

interface HymnalContentService {

    suspend fun getIndexSections(): Result<List<IndexSection>>
}