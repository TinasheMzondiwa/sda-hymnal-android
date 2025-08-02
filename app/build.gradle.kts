plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
    id("dev.zacsweers.metro")
}

foundry {
    features {
        compose()
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

    compileOptions {
        val javaVersion = libs.versions.jvmTarget.get().let(JavaVersion::toVersion)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

ksp { arg("circuit.codegen.mode", "metro") }

dependencies {
    ksp(libs.circuit.codegen)

    implementation(libs.androidx.activity.compose)
    implementation(libs.google.android.material)
    implementation(libs.material3.adaptive.navigation.suite)
    implementation(libs.timber)
    implementation(projects.features.search)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.navigation.api)
    implementation(projects.services.storage)
}