plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("dev.zacsweers.metro")
}

android {
    namespace = "hymnal.storage"
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }

dependencies {
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.runtime)
}