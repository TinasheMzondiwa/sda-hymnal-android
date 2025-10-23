plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.ksp)
}

foundry { features { metro() } }

dependencies {
    api(platform(libs.google.firebase.bom))
    api(libs.google.firebase.analytics)
    api(libs.google.firebase.auth)
    api(libs.google.firebase.config)
    api(libs.google.firebase.crashlytics)
    api(libs.google.firebase.firestore)
    implementation(libs.timber)
}