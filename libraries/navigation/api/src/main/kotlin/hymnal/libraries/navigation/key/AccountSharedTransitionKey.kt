// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.navigation.key

data class AccountSharedTransitionKey(val type: ElementType) {
    enum class ElementType(val id: String) {
        Card("account_card"),
        Image("account_image"),
        Name("account_name"),
        Email("account_email"),
    }
}
