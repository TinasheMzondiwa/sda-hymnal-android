// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
}

foundry {
    features {
        compose()
        metro()
    }
}

ksp { arg("circuit.codegen.mode", "metro") }

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlin.coroutines.playservices)
    implementation(libs.play.services.location)
    implementation(libs.timber)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.l10n)
    implementation(projects.libraries.navigation.api)
    implementation(projects.services.prefs.api)
    implementation(projects.services.sabbath.api)

    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
    testImplementation(libs.test.turbine)
    testImplementation(projects.libraries.foundation.coroutines.test)

    ksp(libs.circuit.codegen)
}