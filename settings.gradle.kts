fun RepositoryHandler.tomtomArtifactory() {
    maven("https://repositories.tomtom.com/artifactory/maven") {
        content { includeGroupByRegex("com\\.tomtom\\..+") }
    }
}

pluginManagement {
    repositories {
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
        tomtomArtifactory()
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
    }
}

rootProject.name = "Android-Kotlin"
include(":app")
include(":Base")
//include(":Map")
include(":Utils")
//include(":Http")
include(":Widgets")
