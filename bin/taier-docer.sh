#!/bin/bash

# 获得根目录
SH_FILE_PATH=$(cd `dirname $0`; pwd)
TAIER_PATH=$(cd $SH_FILE_PATH; cd ../;pwd)
BUILD_TARER_SH="mvn-build.sh"
TAIER_WEB_PATH=$(cd $TAIER_PATH; cd taier-ui/;pwd)

echo "当前文件路径:$SH_FILE_PATH"
echo "taier路径:$TAIER_PATH"

# 打包
echo "开始taier打包"
#.$TAIER_PATH/build/$BUILD_TARER_SH

echo "开始taier-ui编译打包"
cd $TAIER_WEB_PATH
echo "当前执行路径:$TAIER_WEB_PATH"
# 需要提前安装 yarn 环境

yarn build
echo "完成taier-ui编译打包"
echo "完成taier打包"

