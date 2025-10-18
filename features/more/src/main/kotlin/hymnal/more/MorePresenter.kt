// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.more

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.model.HymnalAppConfig
import hymnal.libraries.navigation.DonateScreen
import hymnal.libraries.navigation.MoreScreen
import timber.log.Timber
import hymnal.libraries.l10n.R as L10nR

@AssistedInject
class MorePresenter(
    @Assisted private val navigator: Navigator,
    private val appConfig: HymnalAppConfig,
) : Presenter<State> {

    private val appVersion: String by lazy { "${appConfig.version}(${appConfig.buildNumber})" }

    @CircuitInject(MoreScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): MorePresenter
    }

    @Composable
    override fun present(): State {
        return State(
            appVersion = appVersion,
            eventSink = { event ->
                when (event) {
                    is Event.OnDonateClick -> {
                        navigator.goTo(DonateScreen)
                    }
                    is Event.OnLinkClick -> {
                        handleLinkClick(event)
                    }
                }
            }
        )
    }

    private fun handleLinkClick(event: Event.OnLinkClick) {
        when (event.link) {
            InfoLink.Feedback -> sendFeedback(event.context)
            InfoLink.ShareApp -> shareApp(event.context)
            InfoLink.Rate,
            InfoLink.Review -> openPlayStore(event.context)
            InfoLink.PrivacyPolicy -> openPolicy(event.context)
        }
    }

    private fun sendFeedback(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri() // only email apps should handle this
            putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(context.getString(L10nR.string.app_email))
            )
            putExtra(
                Intent.EXTRA_SUBJECT,
                "${context.getString(L10nR.string.app_name)} v${appConfig.version}"
            )
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // No email app available, do nothing for now
            Timber.e(e)
        }
    }

    private fun shareApp(context: Context) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,
                context.getString(
                    L10nR.string.app_share_message,
                    context.getString(L10nR.string.app_link)
                )
            )
            type = "text/plain"
        }
        val chooser = Intent.createChooser(shareIntent, "Share via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun openPlayStore(context: Context) {
        val packageName = "com.tinashe.sdah"
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$packageName".toUri())
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private fun openPolicy(context: Context) {
        val url = context.getString(L10nR.string.app_policy)
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}