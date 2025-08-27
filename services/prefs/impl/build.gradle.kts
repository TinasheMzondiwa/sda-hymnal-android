plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("dev.zacsweers.metro")
}

dependencies {
    api(projects.services.prefs.api)
    implementation(libs.androidx.datastore.prefs)
}