package hymnal.sing.components

import androidx.compose.runtime.Immutable
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class HymnContent(
    val index: String,
    val number: Int,
    val title: String,
    val majorKey: String?,
    val lyrics: ImmutableList<HymnLyrics>,
) {
    constructor(hymn: Hymn) : this(
        index = hymn.index,
        number = hymn.number,
        title = hymn.title,
        majorKey = hymn.majorKey,
        lyrics = hymn.lyrics.toImmutableList()
    )
}
