plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.parcelize)
}

dependencies {
    api(libs.bundles.circuit)
}