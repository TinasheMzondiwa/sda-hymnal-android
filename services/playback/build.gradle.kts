plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}
android {
    namespace = "hymnal.services.playback"
}
foundry {
    features { compose() }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.androidx.core)
    implementation(libs.timber)
    implementation(projects.libraries.foundation.coroutines)
    implementation(projects.libraries.l10n)
}