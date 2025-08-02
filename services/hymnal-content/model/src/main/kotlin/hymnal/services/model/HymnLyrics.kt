package hymnal.services.model

sealed interface HymnLyrics {
    val index: Int
    val lines: List<String>

    data class Verse(
        override val index: Int,
        override val lines: List<String>,
    ) : HymnLyrics

    data class Chorus(
        override val index: Int,
        override val lines: List<String>,
    ) : HymnLyrics
}