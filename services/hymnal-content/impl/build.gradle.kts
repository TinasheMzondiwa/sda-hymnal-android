plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("dev.zacsweers.metro")
}

android {
    namespace = "hymnal.content.impl"
}

dependencies {
    implementation(projects.services.hymnalContent.api)
}