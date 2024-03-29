plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.ksp)
    alias(libs.plugins.sgp.base)
}

android {
    namespace = "hymnal.storage"
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }

slack {
    features { dagger() }
}

dependencies {
    implementation(projects.libraries.di)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.dagger.runtime)
    kapt(libs.dagger.apt.compiler)
}