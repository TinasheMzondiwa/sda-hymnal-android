// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
}

foundry { features { compose() } }

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.annotations)
    implementation(libs.androidx.compose.runtime)
}