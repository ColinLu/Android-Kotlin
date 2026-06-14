# 📦 模块化发布指南

本项目的 Library 模块可以发布到以下仓库供其他项目引用:
- **本地仓库** (开发测试)
- **Gitee Maven** (国内快速访问)
- **GitHub Packages** (国际访问)

---

## 🚀 快速开始

### 1️⃣ 配置发布目标

在 `local.properties` 文件中添加以下配置(参考 `local.properties.template`):

```properties
# 选择发布目标: local / gitee / github
publish.target=local

# 通用配置
publish.group=com.colin.library.android
publish.version=0.0.2

# Gitee 配置(如果发布到 Gitee)
gitee.url=https://gitee.com/ColinTeam/maven/raw/master/repository
gitee.user=你的Gitee用户名
gitee.pwd=你的Gitee私人令牌

# GitHub 配置(如果发布到 GitHub)
github.user=你的GitHub用户名
github.token=你的GitHub Token
```

### 2️⃣ 发布命令

```bash
# 发布到本地仓库
./gradlew :Utils:publish
./gradlew :Widgets:publish
./gradlew :Network:publish

# 发布到 Gitee
./gradlew :Utils:publish -PpublishTarget=gitee

# 发布到 GitHub
./gradlew :Utils:publish -PpublishTarget=github

# 一次性发布所有模块
./gradlew publish -PpublishTarget=local
```

---

## 📋 在其他项目中引用

### 方式一: 从本地仓库引用

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven { url = uri("/Users/Colin/Projects/Maven/Repository") }
    }
}

// build.gradle.kts
dependencies {
    implementation("com.colin.library.android:utils:0.0.2")
    implementation("com.colin.library.android:widgets:0.0.2")
    implementation("com.colin.library.android:network:0.0.2")
}
```

### 方式二: 从 Gitee 引用

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://gitee.com/ColinTeam/maven/raw/master/repository")
            credentials {
                username = "你的Gitee用户名"
                password = "你的Gitee私人令牌"
            }
        }
    }
}

// build.gradle.kts
dependencies {
    implementation("com.colin.library.android:utils:0.0.2@aar")
    implementation("com.colin.library.android:widgets:0.0.2@aar")
}
```

### 方式三: 从 GitHub Packages 引用

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/your-username/Android-Kotlin")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

// build.gradle.kts
dependencies {
    implementation("com.colin.library.android:utils:0.0.2")
}
```

---

## 🔧 自定义模块 Artifact ID

默认情况下,artifactId 使用模块名称的小写形式。如需自定义:

### 方法一: 在模块的 build.gradle.kts 中设置

```kotlin
// Utils/build.gradle.kts
extra["PUBLISH_ARTIFACT"] = "my-utils"
apply(from = rootProject.file("publish-config.gradle.kts"))
```

### 方法二: 通过命令行参数

```bash
./gradlew :Utils:publish -PPUBLISH_ARTIFACT=my-utils
```

---

## 📝 版本管理建议

### 版本号规范

遵循 [语义化版本](https://semver.org/lang/zh-CN/):

- **主版本号**: 不兼容的 API 修改
- **次版本号**: 向下兼容的功能新增
- **修订号**: 向下兼容的问题修正

示例: `1.0.0` → `1.0.1` → `1.1.0` → `2.0.0`

### 发布流程

1. **开发阶段**: 使用 SNAPSHOT 版本
   ```properties
   publish.version=0.0.2-SNAPSHOT
   ```

2. **正式发布**: 移除 SNAPSHOT
   ```properties
   publish.version=0.0.2
   ```

3. **更新版本号**: 修改 `local.properties` 或 `gradle/libs.versions.toml`

---

## ⚠️ 注意事项

### 安全配置

- ❌ **不要**将 `local.properties` 提交到 Git
- ✅ **使用**环境变量存储敏感信息
- ✅ **定期**更新 Access Token

### Gitee 配置要点

1. 生成私人令牌: https://gitee.com/profile/personal_access_tokens
2. 确保令牌有 `projects` 权限
3. 仓库必须是公开的和/或你有写入权限

### GitHub Packages 配置要点

1. 生成 Token: https://github.com/settings/tokens
2. 需要勾选 `read:packages` 和 `write:packages` 权限
3. 仓库名必须与 GitHub 仓库完全匹配(大小写敏感)

---

## 🐛 常见问题

### Q: 发布失败 "Connection refused"
A: 检查代理配置,在 `gradle.properties` 中禁用代理:
```properties
systemProp.http.proxyHost=
systemProp.https.proxyHost=
systemProp.socksProxyHost=
```

### Q: Gitee 认证失败
A: 确认用户名和密码/令牌正确,注意密码中特殊字符需要 URL 编码

### Q: 依赖冲突
A: 使用 `compileOnly` 而非 `implementation` 避免传递依赖:
```kotlin
dependencies {
    compileOnly(project(":Utils"))  // ✅ 不会传递给引用方
}
```

### Q: 如何查看已发布的版本?
A: 
- 本地: 直接查看仓库目录
- Gitee: 访问 Maven 仓库 URL
- GitHub: https://github.com/your-username/Android-Kotlin/packages

---

## 📊 模块依赖关系

```
Utils (基础工具库,无依赖)
  ↑
Widgets (UI组件,依赖 Utils)
  ↑
Network (网络模块,依赖 Utils)
```

发布顺序建议:
```bash
./gradlew :Utils:publish
./gradlew :Widgets:publish
./gradlew :Network:publish
```

---

## 🎯 最佳实践

1. **开发时**: 使用本地仓库,快速迭代
2. **团队共享**: 发布到 Gitee,国内速度快
3. **开源项目**: 同时发布到 GitHub Packages
4. **自动化**: 使用 CI/CD 自动发布(见 `.github/workflows/publish.yml`)
5. **文档**: 每次发布更新 CHANGELOG.md

---

## 📞 支持

如有问题,请提 Issue 或联系: 945919945@qq.com
