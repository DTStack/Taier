---
title: 部署依赖
sidebar_label: 部署依赖
---

# 依赖组件

## 基础软件

- JDK 版本: **JDK 1.8 +**
- MySQL 版本: **MySQL 5.7.33 +**
- Zookeeper 版本: **Zookeeper 3.5.7 +**

## Web 浏览器要求

- 推荐使用 `Chrome`

## 三方框架

- Chunjun（数据同步插件）：[**1.12**](https://github.com/DTStack/chunjun/tags)

> [Chunjun](https://github.com/DTStack/chunjun) 是一个基于 Flink 的批流统一的数据同步工具，既可以采集静态的数据。 比如 MySQL，HDFS 等，也可以采集实时变化的数据，比如 MySQL binlog，Kafka 等。

:::tip 
Taier使用Chunjun来实现数据同步、实时采集等功能，Flink组件的配置参数依赖Chunjun的插件包路径
:::

## 大数据组件

- [Flink](https://flink.apache.org/)
  - 数据同步任务依赖
  - 版本: `1.12.7`
  - 地址: https://archive.apache.org/dist/flink/flink-1.12.7/

:::tip 
Flink组件的flinkLibDir配置依赖Flink相关的jar包，建议参考文档目录进行下载配置
:::

- [Spark](https://spark.apache.org/)
  - Spark SQL 任务运行依赖
  - 版本：`spark2.1.3`
  - 地址: https://archive.apache.org/dist/spark/spark-2.1.3/spark-2.1.3-bin-hadoop2.7.tgz 
  - 版本：`spark3.2.0`
  - 地址: https://archive.apache.org/dist/spark/spark-3.2.0/spark-3.2.0-bin-hadoop2.7.tgz

:::tip 
Spark组件的sparkYarnArchive配置依赖spark相关的jar包，建议参考文档目录进行下载配置  
Hadoop集群版本为3以上时候，请下载为Hadoop3相关版本
:::
