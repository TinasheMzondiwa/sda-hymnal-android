// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.navigation

import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingHymnScreen(val index: String, val source: Source) : Screen {

    /** Source of the hymn for analytics. */
    enum class Source {
        COLLECTION,
        DEEP_LINK,
        HOME,
        LAST_OPENED_HYMN,
        NUMBER_PICKER,
        SABBATH,
        SEARCH_HISTORY,
        SEARCH_RESULT,
    }
}