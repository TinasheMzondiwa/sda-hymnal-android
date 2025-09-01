package hymnal.services.model

import androidx.compose.runtime.Immutable

@Immutable
data class Hymn(
    val index: String,
    val number: Int,
    val title: String,
    val majorKey: String?,
    val lyrics: List<HymnLyrics>,
)
