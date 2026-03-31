plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
}

foundry {
    features { metro() }
}

dependencies {
    api(libs.metrox.android)
    implementation(libs.androidx.work)
}