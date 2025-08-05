plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
}

android { namespace = "hymnal.ui" }

foundry {
    features { compose() }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.bundles.compose)
    api(libs.bundles.compose.tooling)
    api(libs.bundles.coil)

    implementation(libs.androidx.core)
    implementation(libs.timber)
}