// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.hymnal.R
import com.slack.circuit.runtime.screen.Screen
import hymnal.libraries.navigation.CollectionsScreen
import hymnal.libraries.navigation.HymnsScreen
import hymnal.libraries.navigation.SabbathScreen
import hymnal.libraries.l10n.R as L10nR
import hymnal.ui.R as UiR

enum class HomeRoute(
    val icon: RouteIcon,
    @param:StringRes val title: Int
) {
    Hymns(
        RouteIcon.Hymns,
        L10nR.string.hymns,
    ),

    Collections(
        RouteIcon.Collections,
        L10nR.string.collections,
    ),

    Sabbath(
        RouteIcon.Sabbath,
        L10nR.string.sabbath,
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

        val Collections = RouteIcon(
            icon = R.drawable.ic_list_heart,
            filledIcon = R.drawable.ic_list_heart_fill,
        )

        val Sabbath = RouteIcon(
            icon = UiR.drawable.ic_sunset,
            filledIcon = UiR.drawable.ic_sunset_fill,
        )
    }
}

fun HomeRoute.screen(): Screen {
    return when (this) {
        HomeRoute.Hymns -> HymnsScreen
        HomeRoute.Collections -> CollectionsScreen
        HomeRoute.Sabbath -> SabbathScreen
    }
}