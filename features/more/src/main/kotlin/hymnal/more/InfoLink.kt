// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.more

import androidx.annotation.DrawableRes
import hymnal.more.R as MoreR
import androidx.annotation.StringRes
import hymnal.libraries.l10n.R as L10nR

enum class InfoLink(
    @param:DrawableRes val icon: Int,
    @param:StringRes val title: Int,
) {
    Feedback(
        icon = MoreR.drawable.ic_help,
        title = L10nR.string.info_help_feedback,
    ),
    ShareApp(
        icon = MoreR.drawable.ic_share,
        title = L10nR.string.info_share_app,
    ),
    Rate(
        icon = MoreR.drawable.ic_star_rate,
        title = L10nR.string.info_rate_app,
    ),
    Review(
        icon = MoreR.drawable.ic_rate_review,
        title = L10nR.string.info_write_review,
    ),
    PrivacyPolicy(
        icon = MoreR.drawable.ic_policy,
        title = L10nR.string.info_privacy_policy,
    ),
}
