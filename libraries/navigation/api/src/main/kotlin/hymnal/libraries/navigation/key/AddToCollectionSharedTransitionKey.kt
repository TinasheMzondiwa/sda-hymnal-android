// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.navigation.key

data class AddToCollectionSharedTransitionKey(
    val id: String = "AddToCollection",
    val type: ElementType,
) {
    enum class ElementType {
        CreateButton,
        TopAppBar,
        Content,
    }
}
