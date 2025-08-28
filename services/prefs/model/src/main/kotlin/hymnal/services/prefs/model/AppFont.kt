package hymnal.services.prefs.model

enum class AppFont(val label: String) {
    POPPINS(label = "Poppins"),
    LATO(label = "Lato"),
    PROXIMA_NOVA(label = "Proxima Nova"),
    ANDADA(label = "Andada"),
    GENTIUM_BOOK(label = "Gentium Book"),
    ADVENT_SANS(label = "Advent Sans");

    companion object {
        fun fromName(name: String?): AppFont = name?.let {
            entries.associateBy(AppFont::name)[it] ?: POPPINS
        } ?: POPPINS
    }
}