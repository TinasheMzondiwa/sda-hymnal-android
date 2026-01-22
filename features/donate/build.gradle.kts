import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
}

extensions.configure<LibraryExtension> { namespace = "hymnal.donate" }

foundry {
    features {
        compose()
        metro()
    }
}

ksp { arg("circuit.codegen.mode", "metro") }

dependencies {
    implementation(libs.android.billing)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.immutable)
    implementation(libs.timber)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.foundation.coroutines)
    implementation(projects.libraries.l10n)
    implementation(projects.libraries.model)
    implementation(projects.libraries.navigation.api)

    ksp(libs.circuit.codegen)
}