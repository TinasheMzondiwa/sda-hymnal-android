plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    api(projects.libraries.foundation.coroutines)
    api(projects.services.prefs.model)
}