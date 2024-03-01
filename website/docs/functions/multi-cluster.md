---
title: 集群配置
sidebar_label: 集群配置
---

## 集群配置
1. 集群配置按照`公共组件`-->`资源调度组件`-->`存储组件`-->`计算组件`的顺序进行配置
2. 不同厂商Hadoop集群环境各不一样，遇到适配问题，可以尝试修改Taier源码支持

## 环境依赖
- [x] SFTP服务器


## 组件依赖
| 配置组件   | 依赖组件 |
| --------- | ------- |
| SFTP      |  |
| YARN      | SFTP |
| HDFS      | SFTP、YARN |
| Spark     | SFTP、YARN、HDFS |
| Script(on-yarn)     | SFTP、YARN、HDFS |
| Script(on-standalone)     | SFTP |
| Flink(on-yarn)     | SFTP、YARN、HDFS |
| Flink(on-standalone)     | SFTP |


### 组件任务支持关系

| 任务类型    | 依赖组件  | 支持向导模式 |支持脚本模式|
| --------- | -------- |---------|---------|
| 数据同步    | Flink   | ✅ |✅|
| Flink SQL    | Flink   | ✅ |✅|
| Flink Jar    | Flink   |  | |
| 实时采集    | Flink   | ✅ |✅|
| Spark SQL    | Spark、Spark-Thrift   |  | |
| Spark Jar    | Spark   |  | |
| Python    | Script   |  | |
| Shell    | Script   |  | | 
| DataX    | DataX   |  |✅|

:::tip 
组件类型的任务 需要先在集群配置好对应的组件才能使用   
向导模式可以通过页面交互的方式来完成任务的配置，不需要关心不同任务的json信息如何配置，但`强依赖Taier开发支持`   
脚本模式可以直接使用`对应任务的json`来执行
:::