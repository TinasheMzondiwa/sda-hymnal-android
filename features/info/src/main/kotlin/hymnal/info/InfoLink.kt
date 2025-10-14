// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.info
import hymnal.info.R as InfoR

enum class InfoLink(
    val icon: Int,
    val title: String,
) {
    Feedback(
        icon = InfoR.drawable.ic_help,
        title = "Help & Feedback",
    ),
    ShareApp(
        icon = InfoR.drawable.ic_share,
        title = "Share the App",
    ),
    Rate(
        icon = InfoR.drawable.ic_star_rate,
        title = "Rate the App",
    ),
    Review(
        icon = InfoR.drawable.ic_rate_review,
        title = "Write a Review",
    ),
    PrivacyPolicy(
        icon = InfoR.drawable.ic_policy,
        title = "Privacy Policy",
    ),
}
