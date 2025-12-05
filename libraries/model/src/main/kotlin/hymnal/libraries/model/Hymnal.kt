// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.model

enum class Hymnal(val year: String, val title: String) {
    OldHymnal("1941", "The Church Hymnal (1941)"),
    NewHymnal("1985", "Seventh-day Adventist Hymnal (1985)");

    companion object Companion {
        fun fromYear(year: String): Hymnal? = entries.associateBy(Hymnal::year)[year]
    }
}