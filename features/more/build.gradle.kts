plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
}

android { namespace = "hymnal.more" }

foundry {
    features {
        compose()
        metro()
    }
}

ksp { arg("circuit.codegen.mode", "metro") }

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.kotlinx.immutable)
    implementation(libs.timber)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.l10n)
    implementation(projects.libraries.model)
    implementation(projects.libraries.navigation.api)

    ksp(libs.circuit.codegen)
}