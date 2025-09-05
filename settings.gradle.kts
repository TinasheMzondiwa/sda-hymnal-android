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
    ":features:collections",
    ":features:hymns",
    ":features:sing",
    ":libraries:core-ui",
    ":libraries:foundation:coroutines",
    ":libraries:l10n",
    ":libraries:navigation:api",
    ":libraries:navigation:number",
    ":services:hymnal-content:api",
    ":services:hymnal-content:impl",
    ":services:hymnal-content:model",
    ":services:prefs:api",
    ":services:prefs:impl",
    ":services:prefs:model",
    ":services:storage"
)
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":features:sabbath")
