import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
}

extensions.configure<LibraryExtension> {
    namespace = "hymnal.libraries.l10n"
}
