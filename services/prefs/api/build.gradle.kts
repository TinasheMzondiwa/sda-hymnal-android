plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
}

dependencies {
    api(projects.libraries.foundation.coroutines)
    api(projects.libraries.model)
    api(projects.services.prefs.model)
}