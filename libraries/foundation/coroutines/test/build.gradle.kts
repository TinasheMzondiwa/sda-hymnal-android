// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    implementation(libs.test.coroutines)
    implementation(libs.test.junit)
    implementation(projects.libraries.foundation.coroutines)
}