import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

extensions.configure<LibraryExtension> {
    namespace = "hymnal.storage"
}

foundry { features { metro() } }

ksp { arg("room.schemaLocation", "$projectDir/schemas") }

dependencies {
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.runtime)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.services.hymnalContent.model)

    ksp(libs.androidx.room.compiler)
}