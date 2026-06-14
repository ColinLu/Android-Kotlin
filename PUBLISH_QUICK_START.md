# 🚀 模块发布 - 快速开始

## 📋 前置准备

### 1. 配置 local.properties

复制模板文件并配置:

```bash
cp local.properties.template local.properties
```

编辑 `local.properties`,填入你的配置:

```properties
# 发布目标: local(本地) / gitee / github
publish.target=local

# 版本号
publish.version=0.0.2

# Gitee 配置(如需要)
gitee.user=你的Gitee用户名
gitee.pwd=你的Gitee私人令牌
```

---

## 🎯 三种使用方式

### 方式一: 一键脚本(推荐) ⭐

```bash
# 发布到本地(默认)
./publish.sh

# 发布到本地,指定版本
./publish.sh local 1.0.0

# 发布到 Gitee
./publish.sh gitee

# 发布到 GitHub
./publish.sh github
```

**特点:**
- ✅ 自动构建所有模块
- ✅ 彩色输出,清晰易读
- ✅ 交互式确认
- ✅ 显示引用示例

---

### 方式二: Gradle 命令

```bash
# 发布单个模块到本地
./gradlew :Utils:publish

# 发布所有模块到 Gitee
./gradlew publish -PpublishTarget=gitee

# 发布时指定版本
./gradlew :Widgets:publish -PPUBLISH_VERSION=1.0.0
```

---

### 方式三: Android Studio

1. 打开 Gradle 面板
2. 找到模块的 `publish` 任务
3. 双击运行

例如: `Utils > Tasks > publishing > publish`

---

## 📦 引用发布的库

### 本地仓库

```kotlin
// settings.gradle.kts
repositories {
    maven { url = uri("/Users/Colin/Projects/Maven/Repository") }
}

// build.gradle.kts
dependencies {
    implementation("com.colin.library.android:utils:0.0.2")
    implementation("com.colin.library.android:widgets:0.0.2")
}
```

### Gitee Maven

```kotlin
repositories {
    maven {
        url = uri("https://gitee.com/ColinTeam/maven/raw/master/repository")
        credentials {
            username = "你的Gitee用户名"
            password = "你的Gitee私人令牌"
        }
    }
}

dependencies {
    implementation("com.colin.library.android:utils:0.0.2@aar")
}
```

---

## 🔄 典型工作流程

### 开发阶段

```bash
# 1. 修改代码
# 2. 发布到本地测试
./publish.sh local 0.0.2-SNAPSHOT

# 3. 在其他项目中引用测试
```

### 正式发布

```bash
# 1. 更新版本号
# 2. 发布到 Gitee/GitHub
./publish.sh gitee 1.0.0

# 3. 通知团队成员
```

---

## 📊 可用模块

| 模块 | Artifact ID | 说明 | 依赖 |
|------|-------------|------|------|
| Utils | utils | 工具类库 | 无 |
| Widgets | widgets | UI组件库 | Utils |
| Network | network | 网络模块 | Utils |

---

## ⚡ 常用命令速查

```bash
# 查看帮助
cat PUBLISH_GUIDE.md

# 清理构建
./gradlew clean

# 构建 Release
./gradlew assembleRelease

# 发布到本地
./publish.sh

# 发布到 Gitee,指定版本
./publish.sh gitee 1.0.0

# 只发布 Utils 模块
./gradlew :Utils:publish

# 强制刷新依赖
./gradlew :Utils:publish --refresh-dependencies
```

---

## ❓ 遇到问题?

### Q: Java Runtime 找不到?
```bash
# macOS 安装 JDK 17
brew install openjdk@17
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
```

### Q: Gitee 认证失败?
检查 `local.properties`:
```properties
gitee.user=正确的用户名
gitee.pwd=正确的私人令牌
```

生成令牌: https://gitee.com/profile/personal_access_tokens

### Q: 代理连接错误?
在 `gradle.properties` 中禁用代理:
```properties
systemProp.http.proxyHost=
systemProp.https.proxyHost=
systemProp.socksProxyHost=
```

---

## 📖 更多文档

- **完整指南**: [PUBLISH_GUIDE.md](PUBLISH_GUIDE.md)
- **配置模板**: [local.properties.template](local.properties.template)
- **统一配置**: [publish-config.gradle.kts](publish-config.gradle.kts)

---

## 💡 提示

1. **开发时用本地仓库** - 快速迭代
2. **团队共享用 Gitee** - 国内速度快
3. **开源项目用 GitHub** - 国际访问方便
4. **使用 SNAPSHOT 版本** - 开发阶段避免冲突
5. **定期清理本地仓库** - 节省磁盘空间

---

**祝你发布顺利! 🎉**
