// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.model.HymnalAppConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import hymnal.libraries.l10n.R as L10nR

interface BillingManager {
    fun setup(coroutineScope: CoroutineScope)
    fun products(): Flow<Set<DonateProduct>>
    fun purchaseState(): Flow<BillingData>
    fun sync()
    fun initiatePurchase(product: DonateProduct, activity: Activity)
}

sealed interface BillingData {
    data class Message(val status: Status, val errorRes: Int? = null) : BillingData
    data class DeepLink(val url: String) : BillingData
    data object None : BillingData

    enum class Status { SUCCESS, ERROR; }
}

internal const val SUBS_BASE_URL = "https://play.google.com/store/account/subscriptions"

@ContributesBinding(AppScope::class, binding = binding<BillingManager>())
@Inject
class BillingManagerImpl(
    private val appContext: Context,
    private val appConfig: HymnalAppConfig,
    private val dispatcherProvider: DispatcherProvider,
) : BillingManager, PurchasesUpdatedListener, BillingClientStateListener {

    private var coroutineScope: CoroutineScope? = null
    private var billingClient: BillingClient? = null
    private var purchaseHistory = emptySet<Purchase>()
    private var productDetailsList = mutableSetOf<ProductDetails>()

    private val billingData = MutableStateFlow<BillingData>(BillingData.None)
    private val availableProducts = MutableStateFlow<Set<DonateProduct>>(emptySet())

    override fun setup(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope

        if (billingClient?.isReady != true) {
            billingClient = BillingClient.newBuilder(appContext)
                .setListener(this)
                .enablePendingPurchases(
                    PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
                )
                .build()

            billingClient?.startConnection(this)
        }
    }

    override fun sync() {
        coroutineScope?.launch(dispatcherProvider.default) {
            queryPurchases()
        }
    }

    override fun products(): Flow<Set<DonateProduct>> = availableProducts

    override fun purchaseState(): Flow<BillingData> = billingData

    override fun initiatePurchase(product: DonateProduct, activity: Activity) {
        val client = billingClient
        if (client == null || !client.isReady) {
            coroutineScope?.launch {
                billingData.emit(
                    BillingData.Message(
                        BillingData.Status.ERROR,
                        L10nR.string.error_billing_client_unavailable
                    )
                )
            }
            return
        }

        if (purchaseHistory.find { it.sku == product.sku } != null) {
            coroutineScope?.launch {
                if (product.type == BillingClient.ProductType.SUBS) {
                    val url = "$SUBS_BASE_URL?${product.sku}&package=${appConfig.appId}"
                    billingData.emit(BillingData.DeepLink(url))
                } else {
                    billingData.emit(
                        BillingData.Message(
                            BillingData.Status.ERROR,
                            L10nR.string.error_item_already_owned
                        )
                    )
                }
            }
            return
        }

        val productDetails = productDetailsList.find { it.productId == product.sku } ?: return
        // Ensure we are not passing an empty list or invalid params
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .apply {
                    val offerToken =
                        productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
                    if (offerToken != null) setOfferToken(offerToken)
                }
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = client.launchBillingFlow(activity, flowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            coroutineScope?.launch {
                billingData.emit(
                    BillingData.Message(
                        BillingData.Status.ERROR,
                        L10nR.string.error_billing_client_unavailable
                    )
                )
            }
        }
    }

    private suspend fun queryPurchases() {
        val inAppHistory = billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )?.purchasesList ?: emptyList()


        val subsHistory = billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )?.purchasesList ?: emptyList()


        purchaseHistory = inAppHistory.union(subsHistory)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        coroutineScope?.launch(dispatcherProvider.default) {
            when (result.responseCode) {
                BillingClient.BillingResponseCode.OK if purchases != null -> {
                    purchases.forEach { purchase ->
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            handlePurchase(purchase)
                        }
                        billingData.emit(
                            BillingData.Message(
                                BillingData.Status.SUCCESS,
                                L10nR.string.success_purchase
                            )
                        )
                    }
                }

                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    billingData.emit(
                        BillingData.Message(
                            BillingData.Status.ERROR,
                            L10nR.string.error_item_already_owned
                        )
                    )
                }

                else -> {
                    billingData.emit(BillingData.Message(BillingData.Status.ERROR))
                }
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val productType = productDetailsList.find { it.productId == purchase.sku }?.productType

            if (productType == BillingClient.ProductType.INAPP) {
                // One-time donations must be consumed to be purchasable again
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.consumePurchase(consumeParams)
            } else {
                // Subscriptions just need acknowledgement
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(params)
            }
        }
    }


    override fun onBillingSetupFinished(result: BillingResult) {
        Timber.i("Billing SetupFinished: ${result.responseCode}")

        coroutineScope?.launch(dispatcherProvider.default) {
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                queryOneTimeDonations()
                queryRecurringDonations()
                queryPurchases()
            } else {
                Timber.e(result.debugMessage)
                billingData.emit(
                    BillingData.Message(
                        BillingData.Status.ERROR,
                        L10nR.string.error_billing_client_unavailable
                    )
                )
            }
        }
    }

    private suspend fun queryOneTimeDonations() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(inAppDonations)
            .build()

        val result = billingClient?.queryProductDetails(params)
        val products =
            if (result?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                result.productDetailsList?.sortedBy { it.productId } ?: emptyList()
            } else {
                Timber.e(result?.billingResult?.debugMessage)
                emptyList()
            }
        productDetailsList.addAll(products)
        val donateProducts = products.map {
            DonateProduct(
                it.productId,
                it.productType,
                it.formattedPrice(BillingClient.ProductType.INAPP)
            )
        }

        availableProducts.update { it + donateProducts }
    }

    private suspend fun queryRecurringDonations() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(subscriptions)
            .build()

        val result = billingClient?.queryProductDetails(params)
        val subs =
            if (result?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                result.productDetailsList ?: emptyList()
            } else {
                Timber.e(result?.billingResult?.debugMessage)
                emptyList()
            }
        productDetailsList.addAll(subs)

        val donateProducts = subs.map {
            DonateProduct(
                it.productId,
                it.productType,
                it.formattedPrice(BillingClient.ProductType.SUBS)
            )
        }

        availableProducts.update { it + donateProducts }
    }

    override fun onBillingServiceDisconnected() {
        Timber.e("Billing disconnected")
    }

    private companion object {
        private val inAppDonations = listOf(
            "donate_1",
            "donate_5",
            "donate_10",
            "donate_25",
            "donate_50",
            "donate_100"
        ).map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        private val subscriptions = listOf(
            "subscription_3",
            "subscription_5",
            "subscription_10",
        ).map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
    }
}

/**
 * Get the [Purchase]'s product sku.
 */
private val Purchase.sku: String? get() = products.firstOrNull()

/**
 * Get the [ProductDetails]'s formatted price.
 */
private fun ProductDetails.formattedPrice(
    productType: String
): String = when (productType) {
    BillingClient.ProductType.INAPP -> oneTimePurchaseOfferDetails?.formattedPrice ?: ""
    BillingClient.ProductType.SUBS -> subscriptionOfferDetails?.firstOrNull()
        ?.pricingPhases
        ?.pricingPhaseList
        ?.firstOrNull()
        ?.formattedPrice ?: ""

    else -> ""
}
