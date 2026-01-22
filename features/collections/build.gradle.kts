import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
}

extensions.configure<LibraryExtension> { namespace = "hymnal.collections" }

foundry {
    features {
        compose()
        metro()
    }
}

ksp { arg("circuit.codegen.mode", "metro") }

dependencies {
    implementation(libs.kotlinx.immutable)
    implementation(libs.timber)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.l10n)
    implementation(projects.libraries.navigation.api)
    implementation(projects.services.firebase)
    implementation(projects.services.hymnalContent.api)
    implementation(projects.services.prefs.api)

    ksp(libs.circuit.codegen)
}