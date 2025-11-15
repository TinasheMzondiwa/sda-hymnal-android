// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    api(projects.libraries.foundation.coroutines)
    api(projects.libraries.model)
    implementation(libs.androidx.annotations)
}