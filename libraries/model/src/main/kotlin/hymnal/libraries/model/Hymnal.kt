// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.model

enum class Hymnal(
    val year: String,
    val title: String,
    val hymns: Int,
    val label: String,
) {
    OldHymnal("1941", "Church Hymnal (1941)", 703, "Church Hymnal"),
    NewHymnal("1985", "Seventh-day Adventist Hymnal (1985)", 695, "SDA Hymnal"),
    Choruses("chorus", "Choruses", 68, "Choruses");

    companion object Companion {
        fun fromYear(year: String): Hymnal? = entries.associateBy(Hymnal::year)[year]
    }
}