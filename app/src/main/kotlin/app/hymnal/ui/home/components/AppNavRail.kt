package app.hymnal.ui.home.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.hymnal.R
import app.hymnal.ui.home.AppRoute
import hymnal.ui.previews.DayNightPreviews
import hymnal.ui.theme.HymnalTheme

@Composable
fun AppNavRail(
    currentRoute: AppRoute,
    navigateToHome: () -> Unit,
    navigateToTopicalIndex: () -> Unit,
    navigateToCollections: () -> Unit,
    modifier: Modifier = Modifier,
    headerContent: @Composable (ColumnScope.() -> Unit)? = null
) {
    NavigationRail(
        header = headerContent,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.weight(1f))

        NavigationRailItem(
            label = { Text(stringResource(id = R.string.hymns)) },
            icon = { Icon(Icons.Rounded.Lyrics, null) },
            selected = currentRoute == AppRoute.Hymnal,
            onClick = navigateToHome,
            alwaysShowLabel = false
        )
        NavigationRailItem(
            label = { Text(stringResource(id = R.string.topical_index)) },
            icon = { Icon(Icons.AutoMirrored.Rounded.ViewList, null) },
            selected = currentRoute == AppRoute.TopicalIndex,
            onClick = navigateToTopicalIndex,
            alwaysShowLabel = false
        )
        NavigationRailItem(
            label = { Text(stringResource(id = R.string.collections)) },
            icon = { Icon(Icons.AutoMirrored.Rounded.LibraryBooks, null) },
            selected = currentRoute == AppRoute.Collections,
            onClick = navigateToCollections,
            alwaysShowLabel = false
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@DayNightPreviews
@Composable
private fun PreviewNavRail() {
    HymnalTheme {
        AppNavRail(
            currentRoute = AppRoute.Hymnal,
            navigateToHome = {},
            navigateToTopicalIndex = {},
            navigateToCollections = {},
            headerContent = {
                Spacer(
                    modifier = Modifier
                        .height(54.dp)
                )
            }
        )
    }
}