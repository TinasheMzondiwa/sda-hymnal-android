// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate.billing

import androidx.annotation.Keep
import com.android.billingclient.api.BillingClient

@Keep
data class DonateProduct(
    val sku: String,
    val type: String,
    val price: String,
)


/**
 * Sorts a collection of DonateProduct with the following criteria:
 * 1. Products of type "inapp" come before products of type "subs".
 * 2. Within each type, products are sorted by their numerical price, lowest first.
 *
 * @return A new, sorted List<DonateProduct>.
 */
internal fun Collection<DonateProduct>.sortedByTypeAndPrice(): List<DonateProduct> {
    val typeComparator = compareBy<DonateProduct> {
        it.type != BillingClient.ProductType.INAPP
    }

    val priceComparator = compareBy<DonateProduct> {
        // Remove currency symbols and whitespace, then convert to Double for numeric sorting.
        // Handles prices like "$1.99", "€ 2.99", etc.
        it.price.replace(Regex("[^\\d.,]"), "")
            .replace(",", ".")
            .toDoubleOrNull() ?: Double.MAX_VALUE
    }

    return this.sortedWith(typeComparator.then(priceComparator))
}
