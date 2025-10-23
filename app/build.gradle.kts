// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.services)
}

foundry {
    features {
        compose()
        metro()
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.work)
    implementation(libs.google.android.material)
    implementation(libs.material3.adaptive.navigation.suite)
    implementation(libs.timber)
    implementation(projects.features.account)
    implementation(projects.features.collections)
    implementation(projects.features.donate)
    implementation(projects.features.hymns)
    implementation(projects.features.more)
    implementation(projects.features.sabbath)
    implementation(projects.features.sabbath.widget)
    implementation(projects.features.sing)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.di)
    implementation(projects.libraries.foundation.coroutines)
    implementation(projects.libraries.l10n)
    implementation(projects.libraries.model)
    implementation(projects.libraries.navigation.api)
    implementation(projects.libraries.navigation.number)
    implementation(projects.services.firebase)
    implementation(projects.services.hymnalContent.impl)
    implementation(projects.services.prefs.impl)
    implementation(projects.services.sabbath.impl)
    implementation(projects.services.storage)

    ksp(libs.circuit.codegen)
}