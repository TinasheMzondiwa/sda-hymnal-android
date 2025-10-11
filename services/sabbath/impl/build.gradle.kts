// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

foundry { features { metro() } }

dependencies {
    api(projects.services.sabbath.api)

    implementation(platform(libs.ktor.bom))
    implementation(libs.androidx.annotations)
    implementation(libs.androidx.core)
    implementation(libs.androidx.work)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.timber)
    implementation(projects.libraries.di)
    implementation(projects.libraries.l10n)
    implementation(projects.libraries.model)
    implementation(projects.services.prefs.api)
    implementation(projects.services.storage)

    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
    testImplementation(libs.test.turbine)
    testImplementation(projects.libraries.foundation.coroutines.test)
}