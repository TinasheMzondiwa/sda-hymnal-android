/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package app.hymnal.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.hymnal.R
import com.slack.circuit.runtime.screen.Screen
import hymnal.libraries.navigation.CollectionsScreen
import hymnal.libraries.navigation.HymnsScreen
import hymnal.libraries.navigation.SearchScreen
import hymnal.libraries.l10n.R as L10nR

enum class HomeRoute(
    val icon: RouteIcon,
    @param:StringRes val title: Int
) {
    Hymns(
        RouteIcon.Hymns,
        L10nR.string.hymns,
    ),

    Search(
        RouteIcon.Search,
        L10nR.string.search,
    ),

    Collections(
        RouteIcon.Collections,
        L10nR.string.collections,
    ),
}

data class RouteIcon(
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val filledIcon: Int,
) {
    companion object {
        val Hymns = RouteIcon(
            icon = R.drawable.ic_lyrics,
            filledIcon = R.drawable.ic_lyrics_fill,
        )

        val Search = RouteIcon(
            icon = R.drawable.ic_magnifying_glass,
            filledIcon = R.drawable.ic_magnifying_glass_fill,
        )

        val Collections = RouteIcon(
            icon = R.drawable.ic_list_heart,
            filledIcon = R.drawable.ic_list_heart_fill,
        )
    }
}

fun HomeRoute.screen(): Screen {
    return when (this) {
        HomeRoute.Hymns -> HymnsScreen
        HomeRoute.Collections -> CollectionsScreen
        HomeRoute.Search -> SearchScreen
    }
}