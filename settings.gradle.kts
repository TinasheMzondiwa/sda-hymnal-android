pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "sda-hymnal-android"
include(
    ":app",
    ":features:search",
    ":libraries:core-ui",
    ":libraries:foundation:coroutines",
    ":libraries:navigation:api",
    ":services:hymnal-content:api",
    ":services:hymnal-content:impl",
    ":services:hymnal-content:model",
    ":services:storage"
)
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
