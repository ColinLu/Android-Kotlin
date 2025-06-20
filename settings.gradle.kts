pluginManagement {
    repositories {
        maven { url = uri("https://info-maven.apps.saic-gm.com/repository/maven-public/") }
        maven { url = uri("https://jitpack.io") }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://info-maven.apps.saic-gm.com/repository/maven-public/") }
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
    }
}

rootProject.name = "Android-Kotlin"
include(":app")
include(":Utils")
include(":Widgets")
include(":Network")
