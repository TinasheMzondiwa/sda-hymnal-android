import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

val propertiesFile = rootProject.file("release/api-keys.properties")
val properties = Properties()
if (propertiesFile.exists()) {
    properties.load(propertiesFile.inputStream())
} else {
    println("Warning: release/api-keys.properties not found. BuildConfig fields will use defaults.")
}

android {
    defaultConfig {
        // Define supabase URL and key as build config fields
        buildConfigField("String", "SUPABASE_URL", "\"${properties.getProperty("SUPABASE_URL", "YOUR_URL")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${properties.getProperty("SUPABASE_KEY", "YOUR_KEY")}\"")
    }
    buildFeatures {
        buildConfig = true
    }
}

foundry { features { metro() } }

dependencies {
    api(projects.services.hymnalContent.api)

    implementation(platform(libs.ktor.bom))
    implementation(platform(libs.supabase.bom))
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.storage)
    implementation(libs.timber)
    implementation(projects.services.firebase)
    implementation(projects.services.storage)
}