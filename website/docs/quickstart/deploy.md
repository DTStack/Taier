---
title: 快速部署
sidebar_label: 快速部署
---
# 快速部署
## 基础软件安装
* JDK 8+
* MySQL：5.7.33
* Zookeeper：3.5.7
* Redis：5.0.7

## Taiga 依赖的基础组件
* DatasourceX（数据源插件）：[4.3.0](https://github.com/DTStack/DatasourceX/releases/tag/v4.3.0)

## 根据任务类型需要部署的大数据组件
* [Flink](https://flink.apache.org/)  On Yarn/Standalone
    * 支持数据同步任务
    * 版本：flink-1.10
* [Spark](https://spark.apache.org/)  On Yarn
    * 支持Spark SQL任务
    * 版本：spark-2.1.3
