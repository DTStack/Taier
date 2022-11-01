---
title: 架构设计
sidebar_label: 架构设计
---

# 架构设计

![taier-architecture](/img/readme/taier-architecture.png)

## Taier 与 Chunjun 的关系

- [Chunjun](https://github.com/DTStack/chunjun) 是一个基于 Flink 的批流统一的数据同步工具，既可以采集静态的数据。 
  比如 MySQL，HDFS 等，也可以采集实时变化的数据，比如 MySQL binlog，Kafka 等。
  Taier使用Chunjun来实现数据同步、实时采集等功能。
