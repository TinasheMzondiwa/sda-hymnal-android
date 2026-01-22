plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
}

foundry {
    features { metro() }
}

dependencies {
    implementation(libs.androidx.work)
}