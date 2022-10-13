---
title: 源码编译
sidebar_label: 源码编译
---

# 源码编译

- 下载代码

> git clone https://github.com/DTStack/Taier

- build: 项目编译脚本,根据不同场景来编译项目

```shell
|-- build
|---- mvn-build.sh           //编译项目及任务提交脚本
|---- mvn-build-datadevelop  //编译项目脚本 : Taier后端data-develop启动jar
|---- mvn-build-plugins.sh   //编译插件脚本 : Taier任务提交pluginLibs插件jar
```

- maven构建依赖
  Taier源码编译启动中依赖DatasourceX的core包以及自定义hive的jar包,
  如果本地maven仓库下载失败，可以[手动下载](https://developer.aliyun.com/mvn/search)并install到mvn仓库

```shell
mvn install:install-file -Dfile=datasourcex-core.jar -DgroupId=com.dtstack.dtcenter -DartifactId=common.loader.core -Dversion=1.7.0-SNAPSHOT -Dpackaging=jar
```

```shell
mvn install:install-file -Dfile=dt-insight-hive-shade-4.1.3.jar -DgroupId=dt.insight.plat -DartifactId=dt-insight-hive-shade -Dversion=4.1.3 -Dpackaging=jar
```

:::tip
源码中包含部分scala的代码，通过idea启动需要添加scala的sdk 建议scala版本为`2.11.1`
:::

- 根目录编译

```shell
$ build/mvn-build.sh
```

- 源码检查

  通过脚本编译项目后会生成对应的`lib/taier-data-develop-with-dependencies.jar`和`pluginLibs`源码文件，在项目目录下我们可以检查编译的结果