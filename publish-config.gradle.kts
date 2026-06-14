import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*
import java.util.Properties

/**
 * 统一发布配置脚本
 * 支持发布到: 本地仓库 / Gitee / GitHub Packages
 * 
 * 使用方法:
 * 在 build.gradle.kts 中应用: apply(from = "publish-config.gradle.kts")
 */

// 读取 local.properties 中的发布配置
fun getPublishProperties(project: Project): Properties {
    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    }
    
    return properties
}

// 获取发布目标类型 (local / gitee / github)
fun getPublishTarget(project: Project): String {
    return project.findProperty("publishTarget")?.toString()
        ?: getPublishProperties(project).getProperty("publish.target", "local")
}

// 配置发布
fun configurePublishing(project: Project) {
    val properties = getPublishProperties(project)
    val publishTarget = getPublishTarget(project)
    
    // 确保应用了 maven-publish 插件
    project.apply(plugin = "maven-publish")
    
    project.afterEvaluate {
        val publishing = project.extensions.findByType<PublishingExtension>()
            ?: throw IllegalStateException("Publishing extension not found")
        
        // 配置出版物
        publishing.publications {
            create<MavenPublication>("release") {
                // 从 gradle.properties 或 libs.versions.toml 读取配置
                groupId = project.findProperty("PUBLISH_GROUP")?.toString()
                    ?: properties.getProperty("publish.group", "com.colin.library.android")
                
                artifactId = project.findProperty("PUBLISH_ARTIFACT")?.toString()
                    ?: project.name.lowercase()
                
                version = project.findProperty("PUBLISH_VERSION")?.toString()
                    ?: properties.getProperty("publish.version", "0.0.1")
                
                // 使用 Android 组件
                from(project.components.getByName("release"))
                
                // POM 元数据
                pom {
                    name.set("${project.name} Library")
                    description.set("Android library published by Colin")
                    url.set("https://gitee.com/ColinTeam/Android-Kotlin")
                    
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("colin")
                            name.set("Colin Lu")
                            email.set("945919945@qq.com")
                        }
                    }
                    
                    scm {
                        connection.set("scm:git:https://gitee.com/ColinTeam/Android-Kotlin.git")
                        developerConnection.set("scm:git:ssh://git@gitee.com:ColinTeam/Android-Kotlin.git")
                        url.set("https://gitee.com/ColinTeam/Android-Kotlin")
                    }
                }
            }
        }
        
        // 配置发布仓库
        publishing.repositories {
            when (publishTarget) {
                "local" -> {
                    // 本地仓库
                    maven {
                        url = uri(properties.getProperty("publish.local.path", "${project.rootDir}/../Maven/Repository"))
                        name = "LocalRepo"
                    }
                    println("📦 配置发布到: 本地仓库")
                }
                "gitee" -> {
                    // Gitee Maven 仓库
                    maven {
                        url = uri(properties.getProperty("gitee.url", "https://gitee.com/ColinTeam/maven/raw/master/repository"))
                        name = "GiteeRepo"
                        
                        credentials {
                            username = properties.getProperty("gitee.user")
                                ?: throw IllegalStateException("请在 local.properties 中配置 gitee.user")
                            password = properties.getProperty("gitee.pwd")
                                ?: throw IllegalStateException("请在 local.properties 中配置 gitee.pwd")
                        }
                    }
                    println("🚀 配置发布到: Gitee")
                }
                "github" -> {
                    // GitHub Packages
                    val githubUser = properties.getProperty("github.user")
                        ?: System.getenv("GITHUB_USERNAME")
                        ?: throw IllegalStateException("请在 local.properties 中配置 github.user")
                    
                    maven {
                        url = uri("https://maven.pkg.github.com/${githubUser}/Android-Kotlin")
                        name = "GitHubPackages"
                        
                        credentials {
                            username = githubUser
                            password = properties.getProperty("github.token")
                                ?: System.getenv("GITHUB_TOKEN")
                                ?: throw IllegalStateException("请在 local.properties 中配置 github.token")
                        }
                    }
                    println("🌟 配置发布到: GitHub Packages (${githubUser})")
                }
                else -> {
                    throw IllegalArgumentException("不支持的发布目标: $publishTarget")
                }
            }
        }
    }
}

// 执行配置
configurePublishing(project)
