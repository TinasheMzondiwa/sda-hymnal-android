plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    alias(libs.plugins.sgp.base)
    alias(libs.plugins.anvil)
    alias(libs.plugins.ksp)
}

slack {
    features {
        compose()
        @Suppress("OPT_IN_USAGE")
        dagger(enableComponents = true) { alwaysEnableAnvilComponentMerging() }
    }
}

android {
    namespace = "app.hymnal"

    defaultConfig {
        applicationId = "app.hymnal"
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        buildConfig = true
        compose = true
        resValues = true
    }

    buildTypes {
        val release by getting {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }
    }
}

dependencies {
    ksp(libs.circuit.codegen)

    implementation(projects.libraries.di)
    implementation(projects.libraries.coreUi)
    implementation(projects.services.storage)

    implementation(libs.bundles.circuit)

    implementation(libs.dagger.runtime)
    kapt(libs.dagger.apt.compiler)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.startup)

    implementation(libs.google.android.material)

    implementation(libs.timber)
}