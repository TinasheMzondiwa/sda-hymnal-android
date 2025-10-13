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
    ":features:donate",
    ":features:hymns",
    ":features:info",
    ":features:sabbath",
    ":features:sabbath:widget",
    ":features:sing",
    ":libraries:core-ui",
    ":libraries:di",
    ":libraries:foundation:coroutines",
    ":libraries:foundation:coroutines:test",
    ":libraries:l10n",
    ":libraries:model",
    ":libraries:navigation:api",
    ":libraries:navigation:number",
    ":services:hymnal-content:api",
    ":services:hymnal-content:impl",
    ":services:hymnal-content:model",
    ":services:prefs:api",
    ":services:prefs:impl",
    ":services:prefs:model",
    ":services:sabbath:api",
    ":services:sabbath:impl",
    ":services:storage"
)
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
