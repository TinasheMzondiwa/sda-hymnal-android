plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.parcelize")
    alias(libs.plugins.sgp.base)
}

android {
    namespace = "hymnal.search"
}

slack {
    features {
        compose()
        dagger()
    }
}

dependencies {
    implementation(projects.libraries.di)
    implementation(projects.libraries.coreUi)
}