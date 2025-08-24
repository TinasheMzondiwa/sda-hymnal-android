plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    id("dev.zacsweers.metro")
}

foundry { features { compose() } }

ksp { arg("circuit.codegen.mode", "metro") }

dependencies {
    api(libs.bundles.circuit)
    api(projects.libraries.coreUi)
    implementation(projects.libraries.l10n)

    ksp(libs.circuit.codegen)
}