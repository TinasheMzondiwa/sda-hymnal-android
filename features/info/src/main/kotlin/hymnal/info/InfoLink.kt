// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.info

import androidx.annotation.DrawableRes
import hymnal.info.R as InfoR
import androidx.annotation.StringRes
import hymnal.libraries.l10n.R as L10nR

enum class InfoLink(
    @param:DrawableRes val icon: Int,
    @param:StringRes val title: Int,
) {
    Feedback(
        icon = InfoR.drawable.ic_help,
        title = L10nR.string.info_help_feedback,
    ),
    ShareApp(
        icon = InfoR.drawable.ic_share,
        title = L10nR.string.info_share_app,
    ),
    Rate(
        icon = InfoR.drawable.ic_star_rate,
        title = L10nR.string.info_rate_app,
    ),
    Review(
        icon = InfoR.drawable.ic_rate_review,
        title = L10nR.string.info_write_review,
    ),
    PrivacyPolicy(
        icon = InfoR.drawable.ic_policy,
        title = L10nR.string.info_privacy_policy,
    ),
}
