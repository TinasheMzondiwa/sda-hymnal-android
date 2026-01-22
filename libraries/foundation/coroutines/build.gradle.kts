plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.foundry.base)
}

foundry { features { metro() } }

dependencies {
    api(libs.kotlin.coroutines)
    api(libs.kotlin.coroutines.android)
}