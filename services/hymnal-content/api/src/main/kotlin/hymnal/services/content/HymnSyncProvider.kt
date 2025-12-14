// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content

fun interface HymnSyncProvider {

    /** Maybe sync the hymn if there's new content update. **/
    operator fun invoke(index: String)
}