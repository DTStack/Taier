---
title: 部署依赖
sidebar_label: 部署依赖
---

# 依赖组件

## 基础软件

- JDK 版本: **JDK 1.8 +**
- MySQL 版本: **MySQL 5.7.33 +**
- Zookeeper 版本: **Zookeeper 3.5.7 +**

## 三方框架

- DatasourceX（数据源插件）：[**latest**](https://github.com/DTStack/DatasourceX/tags)
- Chunjun（数据同步插件）：[**1.12**](https://github.com/DTStack/chunjun/tags)

## 大数据组件

- [Flink](https://flink.apache.org/) On Yarn
    - 数据同步任务依赖
    - 版本：`flink-1.12`
- [Spark](https://spark.apache.org/) On Yarn
    - Spark SQL 任务运行依赖
    - 版本：`spark-2.1.3`

## Web 浏览器要求

- 推荐使用 `Chrome`