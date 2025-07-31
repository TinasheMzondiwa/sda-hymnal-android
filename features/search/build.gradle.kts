plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.parcelize")
    alias(libs.plugins.foundry.base)
}

android {
    namespace = "hymnal.search"
}

foundry {
    features {
        compose()
        dagger()
    }
}

dependencies {
    implementation(projects.libraries.di)
    implementation(projects.libraries.coreUi)
}