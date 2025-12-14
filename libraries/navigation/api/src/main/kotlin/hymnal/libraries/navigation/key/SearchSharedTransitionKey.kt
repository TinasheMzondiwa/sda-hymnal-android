// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.navigation.key

data class SearchSharedTransitionKey(val id: String = BUTTON_ID, val type: ElementType) {
    enum class ElementType {
        Button,
        Icon,
    }
    companion object {
        const val BUTTON_ID = "search_button"
        const val ICON_ID = "search_icon"
    }
}
