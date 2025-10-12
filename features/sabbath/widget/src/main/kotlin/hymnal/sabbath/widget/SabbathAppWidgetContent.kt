// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import hymnal.sabbath.widget.data.WidgetSabbathInfo
import hymnal.sabbath.widget.data.WidgetState
import hymnal.sabbath.widget.theme.HymnalGlanceTheme
import hymnal.libraries.l10n.R as L10nR
import hymnal.ui.R as UiR

@Composable
fun SabbathAppWidgetContent(
    state: WidgetState,
    modifier: GlanceModifier = GlanceModifier,
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        titleBar = {
            TitleBar(
                startIcon = ImageProvider(UiR.drawable.ic_sunset_fill),
                title = when (state) {
                    is WidgetState.Loading -> ""
                    is WidgetState.Data -> state.sabbathInfo.location.takeIf { showTitle() } ?: ""
                    is WidgetState.Error -> ""
                }
            )
        }
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .clickable(actionStartActivity(context.launchIntent())),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is WidgetState.Loading -> LoadingContent()
                is WidgetState.Data -> SuccessContent(state)
                is WidgetState.Error -> ErrorContent()
            }
        }
    }
}

@Composable
private fun showTitle(): Boolean {
    return LocalSize.current.width >= 260.dp
}

@Composable
private fun LoadingContent(modifier: GlanceModifier = GlanceModifier) {
    val size = LocalSize.current
    val loadingBarHeight = size.height / 12
    val loadingBarColor = GlanceTheme.colors.secondaryContainer

    Column(
        modifier = modifier.fillMaxSize().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(
            GlanceModifier
                .width(size.width / 3)
                .height(loadingBarHeight)
                .background(loadingBarColor)
                .cornerRadius(8.dp)
        )

        Spacer(GlanceModifier.fillMaxWidth().height(16.dp))

        Spacer(
            GlanceModifier
                .fillMaxWidth()
                .height(loadingBarHeight)
                .background(loadingBarColor)
                .cornerRadius(8.dp)
        )

        Spacer(GlanceModifier.fillMaxWidth().height(16.dp))

        Spacer(
            GlanceModifier
                .width((size.width / 2))
                .height(loadingBarHeight)
                .background(loadingBarColor)
                .cornerRadius(8.dp)
        )

    }
}

@Composable
private fun SuccessContent(state: WidgetState.Data, modifier: GlanceModifier = GlanceModifier) {
    val sabbathInfo = state.sabbathInfo

    Column(
        modifier = modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(GlanceModifier.defaultWeight())

        Text(
            text = sabbathInfo.label,
            modifier = GlanceModifier,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        )

        sabbathInfo.dayLabel?.let {
            Text(
                text = it, modifier = GlanceModifier,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp,
                )
            )
        }

        Text(
            text = sabbathInfo.time,
            style = TextStyle(
                color = GlanceTheme.colors.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(GlanceModifier.defaultWeight())
    }
}

@Composable
private fun ErrorContent(modifier: GlanceModifier = GlanceModifier) {
    Text(
        text = "Tap to update Sabbath times",
        modifier = modifier.fillMaxWidth()
            .padding(8.dp),
        style = TextStyle(
            color = GlanceTheme.colors.onSurfaceVariant,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
    )
}

@SmallWidgetPreview
@Composable
private fun Preview() {
    HymnalGlanceTheme {
        Box(modifier = GlanceModifier.padding(8.dp)) {
            SabbathAppWidgetContent(
                state = WidgetState.Data(
                    sabbathInfo = WidgetSabbathInfo(
                        location = "Springfield, IL",
                        label = "Sabbath starts",
                        dayLabel = "Friday, Oct 4",
                        time = "7:12 PM",
                    )
                )
            )
        }
    }
}

@SmallWidgetPreview
@Composable
private fun PreviewLoading() {
    HymnalGlanceTheme {
        Box(modifier = GlanceModifier.padding(8.dp)) {
            SabbathAppWidgetContent(
                state = WidgetState.Loading
            )
        }
    }
}

@SmallWidgetPreview
@Composable
private fun PreviewError() {
    HymnalGlanceTheme {
        Box(modifier = GlanceModifier.padding(8.dp)) {
            SabbathAppWidgetContent(
                state = WidgetState.Error
            )
        }
    }
}


private fun Context.launchIntent() =
    applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
        ?.apply {
            data =
                "${applicationContext.getString(L10nR.string.app_scheme)}//sabbath".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        } ?: Intent()
