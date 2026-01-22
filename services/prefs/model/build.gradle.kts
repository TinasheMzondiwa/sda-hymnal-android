plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
}

dependencies {
    implementation(libs.androidx.annotations)
    implementation(projects.libraries.l10n)
}