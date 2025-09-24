plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
    alias(libs.plugins.kotlin.android)
}

foundry { features { metro() } }

dependencies {
    api(libs.kotlin.coroutines)
    api(libs.kotlin.coroutines.android)
}