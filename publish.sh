#!/bin/bash

# ==========================================
# 模块化发布脚本
# 用法: ./publish.sh [local|gitee|github] [version]
# ==========================================

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 默认配置
PUBLISH_TARGET=${1:-"local"}
PUBLISH_VERSION=${2:-""}

# 要发布的模块列表
MODULES=("Utils" "Widgets" "Network")

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}📦 Android-Kotlin 模块发布工具${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 显示配置
echo -e "${YELLOW}📋 发布配置:${NC}"
echo -e "  目标仓库: ${GREEN}${PUBLISH_TARGET}${NC}"
if [ -n "$PUBLISH_VERSION" ]; then
    echo -e "  版本号: ${GREEN}${PUBLISH_VERSION}${NC}"
fi
echo ""

# 检查 local.properties
if [ ! -f "local.properties" ]; then
    echo -e "${RED}❌ 错误: 未找到 local.properties 文件${NC}"
    echo -e "${YELLOW}💡 提示: 请复制 local.properties.template 并配置${NC}"
    exit 1
fi

# 确认发布
echo -e "${YELLOW}⚠️  即将发布以下模块:${NC}"
for module in "${MODULES[@]}"; do
    echo -e "  - ${GREEN}${module}${NC}"
done
echo ""
read -p "确认继续? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${RED}❌ 发布已取消${NC}"
    exit 0
fi
echo ""

# 清理构建
echo -e "${BLUE}🧹 清理旧构建...${NC}"
./gradlew clean --quiet

# 构建 Release
echo -e "${BLUE}🔨 构建 Release 版本...${NC}"
for module in "${MODULES[@]}"; do
    echo -e "${YELLOW}  构建 ${module}...${NC}"
    ./gradlew ":${module}:assembleRelease" --quiet
done
echo -e "${GREEN}✅ 构建完成${NC}"
echo ""

# 发布到指定目标
echo -e "${BLUE}🚀 开始发布到 ${PUBLISH_TARGET}...${NC}"
echo ""

for module in "${MODULES[@]}"; do
    echo -e "${YELLOW}📤 发布 ${module}...${NC}"
    
    if [ -n "$PUBLISH_VERSION" ]; then
        ./gradlew ":${module}:publish" \
            -PpublishTarget="${PUBLISH_TARGET}" \
            -PPUBLISH_VERSION="${PUBLISH_VERSION}"
    else
        ./gradlew ":${module}:publish" \
            -PpublishTarget="${PUBLISH_TARGET}"
    fi
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}  ✅ ${module} 发布成功${NC}"
    else
        echo -e "${RED}  ❌ ${module} 发布失败${NC}"
        exit 1
    fi
    echo ""
done

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}🎉 所有模块发布成功!${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 显示引用示例
echo -e "${YELLOW}📖 在其他项目中引用:${NC}"
echo -e "${GREEN}"
echo 'dependencies {'
for module in "${MODULES[@]}"; do
    artifact_id=$(echo "$module" | tr '[:upper:]' '[:lower:]')
    echo "    implementation(\"com.colin.library.android:${artifact_id}:${PUBLISH_VERSION:-0.0.2}\")"
done
echo '}'
echo -e "${NC}"
echo ""

# 如果是本地仓库,显示路径
if [ "$PUBLISH_TARGET" = "local" ]; then
    LOCAL_PATH=$(grep "publish.local.path" local.properties | cut -d'=' -f2)
    if [ -z "$LOCAL_PATH" ]; then
        LOCAL_PATH="${HOME}/Projects/Maven/Repository"
    fi
    echo -e "${YELLOW}📁 本地仓库路径: ${NC}${GREEN}${LOCAL_PATH}${NC}"
    echo ""
fi

echo -e "${YELLOW}💡 提示:${NC}"
echo -e "  - 查看完整文档: ${BLUE}PUBLISH_GUIDE.md${NC}"
echo -e "  - 修改版本: ${BLUE}./publish.sh ${PUBLISH_TARGET} 1.0.0${NC}"
echo -e "  - 发布到 Gitee: ${BLUE}./publish.sh gitee${NC}"
echo ""
