plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
    id("dev.zacsweers.metro")
}

dependencies {
    api(libs.kotlin.coroutines)
    api(libs.kotlin.coroutines.android)
}