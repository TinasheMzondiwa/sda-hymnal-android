plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    api(projects.services.hymnalContent.model)
    api(libs.kotlin.coroutines.android)
}