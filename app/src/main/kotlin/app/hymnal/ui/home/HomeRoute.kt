package app.hymnal.ui.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
import androidx.compose.ui.graphics.vector.ImageVector
import com.slack.circuit.runtime.screen.Screen
import hymnal.libraries.navigation.CollectionsScreen
import hymnal.libraries.navigation.DonateScreen
import hymnal.libraries.navigation.HymnsScreen
import hymnal.libraries.l10n.R as L10nR

enum class HomeRoute(
    val icon: ImageVector,
    @param:StringRes val title: Int
) {
    Hymns(
        Icons.Rounded.Lyrics,
        L10nR.string.hymns,
    ),

    Collections(
        Icons.AutoMirrored.Rounded.LibraryBooks,
        L10nR.string.collections,
    ),

    Donate(
        Icons.Rounded.SentimentVerySatisfied,
        L10nR.string.donate,
    ),
}

fun HomeRoute.screen(): Screen {
    return when (this) {
        HomeRoute.Hymns -> HymnsScreen
        HomeRoute.Collections -> CollectionsScreen
        HomeRoute.Donate -> DonateScreen
    }
}