
plugins {
    alias(libs.plugins.sgp.root)
    alias(libs.plugins.sgp.base)
    alias(libs.plugins.anvil) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.gradle.cache.fix) apply false
    alias(libs.plugins.gradle.retry) apply false
    alias(libs.plugins.sortDependencies) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {
    pluginManager.withPlugin("com.squareup.anvil") {
        dependencies { add("compileOnly", libs.anvil.annotations) }
    }
}

