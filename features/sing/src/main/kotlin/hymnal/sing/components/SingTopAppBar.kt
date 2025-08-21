package hymnal.sing.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PresentToAll
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.ui.theme.HymnalTheme
import hymnal.libraries.l10n.R as L10nR
import hymnal.sing.R as SingR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SingTopAppBar(
    modifier: Modifier = Modifier,
    onNavBack: () -> Unit = { },
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = { Text(text = "") },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onNavBack) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    stringResource(L10nR.string.nav_back)
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Rounded.TextFormat,
                    stringResource(L10nR.string.share)
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    Icons.Rounded.BookmarkBorder,
                    stringResource(L10nR.string.share)
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    painterResource(SingR.drawable.ic_mobile_landscape),
                    stringResource(L10nR.string.share)
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    Icons.Rounded.Share,
                    stringResource(L10nR.string.share)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface { SingTopAppBar(Modifier.padding(16.dp)) }
    }
}