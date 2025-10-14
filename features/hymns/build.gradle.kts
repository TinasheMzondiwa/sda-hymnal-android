plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
}

foundry {
    features {
        compose()
        metro()
    }
}

ksp { arg("circuit.codegen.mode", "metro") }

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.l10n)
    implementation(projects.libraries.navigation.api)
    implementation(projects.libraries.navigation.number)
    implementation(projects.services.hymnalContent.api)

    ksp(libs.circuit.codegen)
}