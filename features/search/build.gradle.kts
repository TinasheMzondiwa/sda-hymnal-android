plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "hymnal.search"
}

foundry {
    features {
        compose()
    }
}

dependencies {
    implementation(projects.libraries.coreUi)
}