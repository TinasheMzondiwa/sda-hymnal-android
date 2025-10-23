plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
}

android { namespace = "hymnal.account" }

foundry {
    features {
        compose()
        metro()
    }
}

ksp { arg("circuit.codegen.mode", "metro") }

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.timber)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.foundation.coroutines)
    implementation(projects.libraries.l10n)
    implementation(projects.libraries.model)
    implementation(projects.libraries.navigation.api)
    implementation(projects.services.firebase)

    ksp(libs.circuit.codegen)
}