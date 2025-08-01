package app.hymnal.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.hymnal.R
import app.hymnal.ui.home.AppRoute
import hymnal.ui.previews.DayNightPreviews
import hymnal.ui.theme.HymnalTheme

@Composable
fun AppDrawer(
    currentRoute: AppRoute,
    navigateToHome: () -> Unit,
    navigateToTopicalIndex: () -> Unit,
    navigateToCollections: () -> Unit,
    closeDrawer: () -> Unit,
    drawerHeaderContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier) {
        drawerHeaderContent()

        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.hymns)) },
            icon = { Icon(Icons.Rounded.Lyrics, null) },
            selected = currentRoute == AppRoute.Hymnal,
            onClick = { navigateToHome(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.topical_index)) },
            icon = { Icon(Icons.AutoMirrored.Rounded.ViewList, null) },
            selected = currentRoute == AppRoute.TopicalIndex,
            onClick = { navigateToTopicalIndex(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.collections)) },
            icon = { Icon(Icons.AutoMirrored.Rounded.LibraryBooks, null) },
            selected = currentRoute == AppRoute.Collections,
            onClick = { navigateToCollections(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.settings)) },
            icon = { Icon(Icons.Rounded.Settings, null) },
            selected = currentRoute == AppRoute.Settings,
            onClick = { closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.feedback)) },
            icon = { Icon(Icons.AutoMirrored.Rounded.Help, null) },
            selected = false,
            onClick = { closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.donate)) },
            icon = { Icon(Icons.Rounded.SentimentVerySatisfied, null) },
            selected = currentRoute == AppRoute.Donate,
            onClick = { closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Made with ❤️",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@DayNightPreviews
@Composable
fun PreviewAppDrawer() {
    HymnalTheme {
        AppDrawer(
            currentRoute = AppRoute.Hymnal,
            navigateToHome = {},
            navigateToTopicalIndex = {},
            navigateToCollections = {},
            closeDrawer = { },
            drawerHeaderContent = {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                )
            }
        )
    }
}

@Composable
private fun NavDrawerSecondaryItem(
    title: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = ButtonDefaults.ContentPadding.calculateTopPadding(),
            end = 16.dp,
            bottom = ButtonDefaults.ContentPadding.calculateBottomPadding()
        )
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@DayNightPreviews
@Composable
private fun PreviewNavDrawerSecondaryItem() {
    HymnalTheme {
        Surface {
            NavDrawerSecondaryItem("Settings") {}
        }
    }
}
