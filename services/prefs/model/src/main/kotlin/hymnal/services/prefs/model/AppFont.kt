package hymnal.services.prefs.model

import kotlin.collections.get

enum class AppFont(val label: String) {
    POPPINS(label = "Poppins"),
    LATO(label = "Lato"),
    PROXIMA_NOVA(label = "Proxima Nova"),
    ANDADA(label = "Andada"),
    ROBOTO(label = "Roboto"),
    GENTIUM_BOOK(label = "Gentium Book");

    companion object {
        fun fromName(name: String?): AppFont = name?.let {
            entries.associateBy(AppFont::name)[it] ?: POPPINS
        } ?: POPPINS
    }
}