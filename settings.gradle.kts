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
    ":libraries:navigation:api",
    ":services:hymnal-content",
    ":services:hymnal-content:impl",
    ":services:storage"
)
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
