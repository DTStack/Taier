---
title: 简介
sidebar_label: 简介
---
# 简介

## 关于 Taiga
Taiga 名字由来中国古代十大名剑之一 —— 太阿。

Taiga 是一个分布式易扩展的可视化DAG工作流任务调度开源系统。解决数据研发ETL 错综复杂的依赖关系，不能直观监控任务健康状态等问题。Taiga 以DAG流式的方式将Task组装起来，可实时监控任务的运行状态，同时支持重试、从指定节点恢复失败、暂停及Kill任务等操作

## 简单易用
* DAG监控页面
  * 运维中心（聚焦项目任务） + 控制台（全局任务监控） 
  * 任务状态、任务类型、重试次数、任务运行机器、可视化变量等关键信息一目了然
* 所有流程定义操作都是可视化的，通过拖拽任务来绘制DAG，配置数据源及资源，同时对于第三方系统提供API方式的操作


## 高可靠性
* 去中心化的分布式模式, 支持HA功能
* 分布式节点 + 两级存储策略 + 队列机制。
  * 每个节点都可以处理任务调度与提交； 
  * 任务多时会优先缓存在内存队列，超出可配置的队列最大数量值后会直接入库持久化； 
  * 任务处理以队列方式消费，队列异步从DB获取可执行实例。

## 丰富的使用场景
* 支持暂停恢复操作，更好的应对大数据的使用场景.
* 支持大数据作业Spark、Flink、Hive、MR的调度，第一版本支持 Spark SQL、Spark MR、PySpark、Flinkx、Flink MR
  * 后续会开源 Python、Shell、Jupyter、Tersorflow、Pytorch、Hadoop MR、Kylin、Odps、SQL(MySQL、PostgreSQL、Hive、SparkSQL、Impala、Oracle、SQLServer、TiDB、greenplum、inceptor、kingbase、presto)、Procedure、Sub_Process
* 支持基于yarn队列实现多集群的任务隔离
* 支持计算组件不同版本

## 高扩展性
* 设计之处就考虑分布式模式，目前支持 整体 Taiga 水平扩容 和 拆分Master/Worker 水平扩容方式；
* 调度能力随集群线性增长；