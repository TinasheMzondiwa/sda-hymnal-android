plugins {
    alias(libs.plugins.android.library)
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

    implementation(libs.google.firebase.appcheck.debug)
    implementation(libs.google.firebase.appcheck.playintegrity)
    implementation(libs.timber)
    implementation(projects.libraries.foundation.coroutines)
    implementation(projects.libraries.model)
}