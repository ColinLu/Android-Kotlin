import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*
import org.gradle.api.tasks.bundling.Jar

// 应用插件（⚠️ 不能使用 plugins { } 块）
project.apply(plugin = "maven-publish")


// 定义源码 jar 包任务
tasks.register<Jar>("sourceJar") {
    // 使用通用方式获取 src/main/java 目录
    from(project.file("src/main/java"))
    archiveClassifier.set("sources")
}

project.afterEvaluate {
    val publishing = project.extensions.findByType<org.gradle.api.publish.PublishingExtension>()
        ?: project.extensions.create<org.gradle.api.publish.PublishingExtension>("publishing")

    publishing.publications {
        register<MavenPublication>("release") {
            groupId = "com.colin.library.android"
            artifactId = "utils"
            version = "0.0.1"

            // 主要 artifact：AAR 文件
            artifact(project.tasks.named("bundleReleaseAar").get())

            // 源码 jar
            artifact(project.tasks.getByName("sourceJar"))

            pom.withXml {
                val root = asNode()

                root.appendNode("name", "Utils Library publish by ColinLu")
                root.appendNode("description", "Android 工具类库")
                root.appendNode("url", "https://gitee.com/ColinTeam/WanWuZhiNan")

                val licensesNode = root.appendNode("licenses")
                val licenseNode = licensesNode.appendNode("license")
                licenseNode.appendNode("name", "The Apache License, Version 2.0")
                licenseNode.appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")

                val developersNode = root.appendNode("developers")
                val developerNode = developersNode.appendNode("developer")
                developerNode.appendNode("id", "ColinLu")
                developerNode.appendNode("name", "ColinLu")
                developerNode.appendNode("email", "945919945@qq.com")

                val scmNode = root.appendNode("scm")
                scmNode.appendNode("connection", "scm:git:https://gitee.com/ColinTeam/WanWuZhiNan.git")
                scmNode.appendNode("developerConnection", "scm:git:ssh://git@gitee.com:ColinTeam/WanWuZhiNan.git")
                scmNode.appendNode("url", "https://gitee.com/ColinTeam/WanWuZhiNan")
            }
        }
    }

    publishing.repositories {
        maven {
            url = uri("/Users/Colin/Projects/Maven/Repository")
        }
    }
}
