package hymnal.hymns.components

import androidx.compose.runtime.Immutable
import hymnal.services.model.Hymn

@Immutable
data class SearchResult(
    val index: String,
    val title: String,
    val number: Int,
) {
    constructor(hymn: Hymn) : this(
        index = hymn.index,
        title = hymn.title,
        number = hymn.number,
    )
}
