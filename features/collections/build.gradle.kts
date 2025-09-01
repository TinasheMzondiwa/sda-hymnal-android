// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
    id("dev.zacsweers.metro")
}

android { namespace = "hymnal.collections" }

foundry { features { compose() } }

ksp { arg("circuit.codegen.mode", "metro") }

dependencies {
    implementation(libs.timber)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.l10n)
    implementation(projects.libraries.navigation.api)
    implementation(projects.services.hymnalContent.api)
    implementation(projects.services.prefs.api)

    ksp(libs.circuit.codegen)
}