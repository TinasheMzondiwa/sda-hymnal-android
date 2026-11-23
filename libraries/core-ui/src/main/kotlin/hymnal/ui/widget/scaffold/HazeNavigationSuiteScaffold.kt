// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.ui.widget.scaffold

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import hymnal.ui.theme.HymnalTheme

interface HazeNavigationSuiteScope {
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        label: @Composable (() -> Unit)? = null,
        alwaysShowLabel: Boolean = true,
        badge: @Composable (() -> Unit)? = null,
        colors: NavigationSuiteItemColors? = null,
        interactionSource: MutableInteractionSource? = null,
    )
}

private class HazeNavigationSuiteItem(
    val selected: Boolean,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val label: @Composable (() -> Unit)?,
    val alwaysShowLabel: Boolean,
    val badge: @Composable (() -> Unit)?,
    val colors: NavigationSuiteItemColors?,
    val interactionSource: MutableInteractionSource?,
)

private class HazeNavigationSuiteScopeImpl : HazeNavigationSuiteScope {
    val items = mutableListOf<HazeNavigationSuiteItem>()

    override fun item(
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        modifier: Modifier,
        enabled: Boolean,
        label: @Composable (() -> Unit)?,
        alwaysShowLabel: Boolean,
        badge: @Composable (() -> Unit)?,
        colors: NavigationSuiteItemColors?,
        interactionSource: MutableInteractionSource?,
    ) {
        items.add(
            HazeNavigationSuiteItem(
                selected = selected,
                onClick = onClick,
                icon = icon,
                modifier = modifier,
                enabled = enabled,
                label = label,
                alwaysShowLabel = alwaysShowLabel,
                badge = badge,
                colors = colors,
                interactionSource = interactionSource,
            )
        )
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun HazeNavigationSuiteScaffold(
    navigationSuiteItems: HazeNavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationSuiteScaffoldDefaults.containerColor,
    contentColor: Color = NavigationSuiteScaffoldDefaults.contentColor,
    state: NavigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState(),
    hazeState: HazeState = remember { HazeState() },
    hazeStyle: HazeStyle = HazeMaterials.regular(MaterialTheme.colorScheme.surface),
    content: @Composable () -> Unit = {},
) {
    val layoutType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfoV2())
    val navigationSuiteColors = NavigationSuiteDefaults.colors(
        shortNavigationBarContainerColor = Color.Transparent,
        navigationBarContainerColor = Color.Transparent,
    )
    val items = HazeNavigationSuiteScopeImpl().apply(navigationSuiteItems).items

    var barVisible by remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (layoutType != NavigationSuiteType.NavigationRail && source == NestedScrollSource.UserInput) {
                    val delta = available.y
                    if (delta < -5f) {
                        barVisible = false
                    } else if (delta > 5f) {
                        barVisible = true
                    }
                }
                return Offset.Zero
            }
        }
    }

    val isNavigationBar = layoutType == NavigationSuiteType.NavigationBar ||
            layoutType == NavigationSuiteType.ShortNavigationBarCompact ||
            layoutType == NavigationSuiteType.ShortNavigationBarMedium

    val isBarVisible = isNavigationBar && barVisible

    val density = LocalDensity.current
    val clampedDensity = remember(density) {
        Density(
            density = density.density,
            fontScale = density.fontScale.coerceAtMost(MaxNavigationFontScale),
        )
    }

    Surface(
        modifier = modifier.nestedScroll(nestedScrollConnection),
        color = containerColor,
        contentColor = contentColor
    ) {
        if (isNavigationBar) {
            Box(modifier = Modifier.fillMaxSize()) {
                CompositionLocalProvider(
                    LocalBottomBarVisible provides isBarVisible,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeSource(state = hazeState)
                    ) {
                        content()
                    }
                }

                AnimatedVisibility(
                    visible = isBarVisible,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    CompositionLocalProvider(LocalDensity provides clampedDensity) {
                        Box(
                            modifier = Modifier
                                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                                .shadow(elevation = 10.dp, shape = FloatingBarShape)
                                .clip(FloatingBarShape)
                                .hazeEffect(state = hazeState, style = hazeStyle)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                    shape = FloatingBarShape,
                                )
                                .padding(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                items.forEach { item ->
                                    FloatingNavigationBarItem(
                                        item = item,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            NavigationSuiteScaffoldLayout(
                navigationSuite = {
                    CompositionLocalProvider(LocalDensity provides clampedDensity) {
                        when (layoutType) {
                            NavigationSuiteType.NavigationRail -> {
                                Row {
                                    NavigationSuite(
                                        layoutType = layoutType,
                                        colors = navigationSuiteColors,
                                        content = {
                                            items.forEach { item ->
                                                item(
                                                    selected = item.selected,
                                                    onClick = item.onClick,
                                                    icon = item.icon,
                                                    modifier = item.modifier,
                                                    enabled = item.enabled,
                                                    label = item.label,
                                                    alwaysShowLabel = item.alwaysShowLabel,
                                                    badge = item.badge,
                                                    colors = item.colors,
                                                    interactionSource = item.interactionSource,
                                                )
                                            }
                                        },
                                    )

                                    VerticalDivider()
                                }
                            }

                            else -> {
                                NavigationSuite(
                                    layoutType = layoutType,
                                    colors = navigationSuiteColors,
                                    content = {
                                        items.forEach { item ->
                                            item(
                                                selected = item.selected,
                                                onClick = item.onClick,
                                                icon = item.icon,
                                                modifier = item.modifier,
                                                enabled = item.enabled,
                                                label = item.label,
                                                alwaysShowLabel = item.alwaysShowLabel,
                                                badge = item.badge,
                                                colors = item.colors,
                                                interactionSource = item.interactionSource,
                                            )
                                        }
                                    },
                                )
                            }
                        }
                    }
                },
                state = state,
                layoutType = layoutType,
                content = {
                    Box(
                        Modifier
                            .hazeSource(state = hazeState)
                            .consumeWindowInsets(
                                if (
                                    state.currentValue == NavigationSuiteScaffoldValue.Hidden &&
                                    !state.isAnimating
                                ) {
                                    NoWindowInsets
                                } else {
                                    when (layoutType) {
                                        NavigationSuiteType.NavigationRail ->
                                            NavigationRailDefaults.windowInsets.only(
                                                WindowInsetsSides.Start
                                            )

                                        NavigationSuiteType.NavigationDrawer ->
                                            DrawerDefaults.windowInsets.only(WindowInsetsSides.Start)

                                        else -> NoWindowInsets
                                    }
                                }
                            )
                    ) {
                        content()
                    }
                },
            )
        }
    }
}

@Composable
private fun FloatingNavigationBarItem(
    item: HazeNavigationSuiteItem,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (item.selected) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f)
    } else {
        Color.Transparent
    }
    val contentColor = if (item.selected) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier
            .then(item.modifier)
            .clip(FloatingBarItemShape)
            .background(backgroundColor)
            .clickable(
                enabled = item.enabled,
                onClick = item.onClick,
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Box(
                modifier = Modifier.size(22.dp),
                contentAlignment = Alignment.Center,
            ) {
                item.icon()
            }
            if (item.label != null && (item.alwaysShowLabel || item.selected)) {
                Spacer(modifier = Modifier.height(2.dp))
                ProvideTextStyle(
                    value = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.5.sp,
                        lineHeight = 12.sp,
                        fontWeight = if (item.selected) FontWeight.SemiBold else FontWeight.Medium,
                        color = contentColor,
                    )
                ) {
                    item.label.invoke()
                }
            }
        }
    }
}

private val FloatingBarShape = RoundedCornerShape(32.dp)
private val FloatingBarItemShape = RoundedCornerShape(26.dp)
private val NoWindowInsets = WindowInsets(0, 0, 0, 0)
private const val MaxNavigationFontScale = 1.0f

@PreviewLightDark
@Composable
private fun HazeNavigationSuiteScaffoldPreview() {
    val navItems = listOf(
        "Home" to Icons.Rounded.FavoriteBorder,
        "Hymns" to Icons.Rounded.Image,
        "Search" to Icons.Rounded.Search,
        "Collections" to Icons.Rounded.Settings,
        "Account" to Icons.Rounded.AccountCircle,
    )

    HymnalTheme {
        HazeNavigationSuiteScaffold(
            navigationSuiteItems = {
                navItems.forEachIndexed { index, (label, icon) ->
                    item(
                        selected = index == 0,
                        onClick = {},
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = {
                            Text(
                                text = label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            },
        ) {
            // content lives here
        }
    }
}

@SuppressLint("ComposeCompositionLocalUsage")
val LocalBottomBarVisible = compositionLocalOf { true }
