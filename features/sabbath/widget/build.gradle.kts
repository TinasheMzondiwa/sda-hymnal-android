// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
}

foundry {
    features {
        compose()
        metro()
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.appwidget.preview)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.work)
    implementation(libs.timber)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.di)
    implementation(projects.libraries.foundation.coroutines)
    implementation(projects.libraries.l10n)

    implementation(projects.services.sabbath.api)
    implementation(projects.services.storage)

    ksp(libs.circuit.codegen)
}