// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.prefs.education

/** Represents an education / orientation to track.**/
sealed class Education(
    val key: String,
    val timesToShow: Int = 1,
    val showAfterDays: Int = 0
) {

    /** Tune playback tooltip **/
    data object TunePlaybackTooltip : Education(
        key = "tune_playback_tooltip_v2",
        timesToShow = 2,
        showAfterDays = 1
    )
}
