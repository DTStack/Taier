---
title: 集群配置
sidebar_label: 集群配置
---

## 集群配置
1. 集群配置按照`公共组件`-->`资源调度组件`-->`存储组件`-->`计算组件`的顺序进行配置
2. 不同厂商Hadoop集群环境各不一样，遇到适配问题，可以尝试修改taier源码支持

## 环境依赖
- [x] SFTP服务器
- [x] Hadoop集群


## 组件依赖
| 配置组件   | 前置组件 |
| --------- | ------- |
| SFTP      |  |
| YARN      | SFTP |
| HDFS      | SFTP、YARN |
| Spark     | SFTP、YARN、HDFS |
| Script     | SFTP、YARN、HDFS |
| Flink-on-yarn     | SFTP、YARN、HDFS |
| Flink-on-standalone     | SFTP |


### 组件任务支持关系

| 任务类型    | 依赖组件  | 是否必须 |
| --------- | -------- |---------|
| 数据同步    | Flink   | 是 |
| Flink SQL    | Flink   | 是 |
| 实时采集    | Flink   | 是 |
| Hive SQL    | Hive   | 是 |
| Spark SQL    | Spark、Spark-Thrift   | 是 |
| Python    | Script   | 是 |
| Shell    | Script   | 是 |