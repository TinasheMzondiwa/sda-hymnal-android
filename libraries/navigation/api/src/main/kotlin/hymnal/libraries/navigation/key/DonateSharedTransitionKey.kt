// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.navigation.key

data class DonateSharedTransitionKey(val id: String = BUTTON_ID, val type: ElementType) {
    enum class ElementType {
        Button,
    }

    companion object {
        const val BUTTON_ID = "donate_button"
    }
}
