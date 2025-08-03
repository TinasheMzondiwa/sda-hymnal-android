package hymnal.services.model

data class Hymn(
    val index: String,
    val number: Int,
    val title: String,
    val majorKey: String?,
    val lyrics: List<HymnLyrics>,
)
