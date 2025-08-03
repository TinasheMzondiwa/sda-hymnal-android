plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("dev.zacsweers.metro")
}

dependencies {
    api(projects.services.hymnalContent.api)

    implementation(platform(libs.supabase.bom))
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.supabase.storage)
    implementation(libs.timber)
    implementation(projects.services.storage)
}