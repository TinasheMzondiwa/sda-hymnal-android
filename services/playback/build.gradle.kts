import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
}
extensions.configure<LibraryExtension> {
    namespace = "hymnal.services.playback"
}
foundry {
    features {
        compose()
        metro()
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core)
    implementation(libs.bundles.compose)
    implementation(libs.timber)
    implementation(projects.libraries.di)
    implementation(projects.libraries.foundation.coroutines)
    implementation(projects.libraries.l10n)
}