---
title: 架构设计
sidebar_label: 架构设计
---

# 架构设计

![taier-architecture](/img/readme/taier-architecture.png)

## Taier 与 DatasourceX、Chunjun 的关系

- [DatasourceX](https://github.com/DTStack/DatasourceX) 是数据源插件，负责各类型数据源的元数据和数据操作，如获取表结构，预览表数据
  等功能均由 DatasourceX 实现；
- [Chunjun](https://github.com/DTStack/chunjun) 是一个基于 Flink 的批流统一的数据同步工具，既可以采集静态的数据，比如
  MySQL，HDFS 等，也可以采集实时变化的数据，比如 MySQL binlog，Kafka 等。
