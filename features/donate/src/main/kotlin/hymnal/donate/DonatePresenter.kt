// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.android.billingclient.api.BillingClient
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuitx.android.IntentScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.donate.billing.BillingData
import hymnal.donate.billing.BillingManager
import hymnal.donate.billing.DonateProduct
import hymnal.donate.billing.SUBS_BASE_URL
import hymnal.donate.billing.sortedByTypeAndPrice
import hymnal.donate.ui.TierButtonSpec
import hymnal.libraries.model.HymnalAppConfig
import hymnal.libraries.navigation.BrowserIntentScreen
import hymnal.libraries.navigation.DonateScreen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import hymnal.libraries.l10n.R as L10nR

@AssistedInject
class DonatePresenter(
    @Assisted private val navigator: Navigator,
    private val billingManager: BillingManager,
    private val appConfig: HymnalAppConfig,
) : Presenter<State> {

    @CircuitInject(DonateScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): DonatePresenter
    }

    @Composable
    override fun present(): State {
        val coroutineScope = rememberStableCoroutineScope()
        LaunchedEffect(Unit) { billingManager.setup(coroutineScope) }

        val products by produceRetainedState(emptySet()) {
            billingManager.products()
                .catch { Timber.e(it) }
                .collect { value = it }
        }
        val data by produceRetainedState<BillingData>(BillingData.None) {
            billingManager.purchaseState()
                .catch { Timber.e(it) }
                .collect { value = it }
        }
        val inAppProducts by produceRetainedState(persistentListOf(), products) {
            value = products.filter { it.type == BillingClient.ProductType.INAPP }.asTier()
        }
        val subscriptions by produceRetainedState(persistentListOf(), products) {
            value = products.filter { it.type == BillingClient.ProductType.SUBS }.asTier()
        }
        var donateType by rememberRetained { mutableStateOf(DonateType.ONE_TIME) }

        var selectedTier by rememberRetained { mutableStateOf<TierButtonSpec?>(null) }

        LaunchedEffect(data) { handleBillingData(data) }

        return when {
            inAppProducts.isEmpty() && subscriptions.isEmpty() -> State.Loading {
                navigator.pop()
            }
            else -> State.Donate(
                type = donateType,
                tiers = when (donateType) {
                    DonateType.ONE_TIME -> inAppProducts
                    DonateType.SUBSCRIPTION -> subscriptions
                },
                selectedTier = selectedTier,
                eventSink = { event ->
                    when (event) {
                        Event.OnClose -> navigator.pop()
                        Event.OnManageSubscriptions ->
                            navigator.goTo(BrowserIntentScreen(SUBS_BASE_URL.toUri()))
                        is Event.OnHelp -> sendFeedback(event.context)
                        is Event.SelectTier -> {
                            selectedTier = event.tier
                        }

                        is Event.OnSelectDonateType -> {
                            donateType = event.type
                            selectedTier = null
                        }

                        is Event.OnPrimaryButtonClick -> {
                            products.firstOrNull { it.sku == selectedTier?.sku }?.let {
                                selectedTier = null
                                billingManager.initiatePurchase(it, event.activity)
                            }
                        }
                    }
                }
            )
        }
    }

    private fun List<DonateProduct>.asTier(): ImmutableList<TierButtonSpec> =
        sortedByTypeAndPrice().map {
            TierButtonSpec(
                sku = it.sku,
                formattedPrice = it.price,
            )
        }.toImmutableList()

    private fun handleBillingData(data: BillingData) = when (data) {
        is BillingData.DeepLink -> navigator.goTo(BrowserIntentScreen(data.url.toUri()))
        is BillingData.Message -> Unit
        BillingData.None -> Unit
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
        navigator.goTo(IntentScreen(intent))
    }
}