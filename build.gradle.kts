
plugins {
    alias(libs.plugins.anvil) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.foundry.root)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.gradle.cache.fix) apply false
    alias(libs.plugins.gradle.retry) apply false
    alias(libs.plugins.sortDependencies) apply false
    alias(libs.plugins.ksp) apply false
}
