package hymnal.services.model

data class Hymn(
    val index: String,
    val number: Int,
    val title: String,
    val lyrics: List<HymnLyrics>,
)
