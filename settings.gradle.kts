pluginManagement {
    repositories {
        // 阿里云镜像(优先)
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        // Gitee 仓库
        maven { url = uri("https://gitee.com/ColinTeam/maven/raw/master/repository") }
        // JitPack
        maven { url = uri("https://jitpack.io") }
        // Google Maven
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // Maven Central
        mavenCentral()
        // Gradle Plugin Portal
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        // 阿里云镜像(优先)
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        // Gitee 仓库
        maven { url = uri("https://gitee.com/ColinTeam/maven/raw/master/repository") }
        // JitPack
        maven { url = uri("https://jitpack.io") }
        // Google Maven
        google()
        // Maven Central
        mavenCentral()
    }
}

rootProject.name = "Android-Kotlin"
include(":app")
include(":Utils")
include(":Widgets")
include(":Network")
include(":Video")
include(":AIDL")
include(":nfc-focus")
