plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.sgp.base)
}

android { namespace = "hymnal.ui" }

slack {
    features { compose() }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.bundles.compose)
    api(libs.bundles.compose.tooling)

    implementation(libs.androidx.core)
    implementation(libs.timber)
}