plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.foundry.base)
}

foundry {
    features {
        daggerRuntimeOnly()
    }
}

dependencies {
    api(libs.dagger.runtime)
    compileOnly(libs.anvil.annotations)
}