import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
}

extensions.configure<LibraryExtension> { namespace = "hymnal.ui" }

foundry {
    features { compose() }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.bundles.coil)
    api(libs.bundles.compose)
    api(libs.bundles.compose.tooling)
    api(libs.bundles.haze)

    implementation(libs.androidx.core)
    implementation(libs.bundles.circuit)
    implementation(libs.material3.adaptive.navigation.suite)
    implementation(libs.timber)
}