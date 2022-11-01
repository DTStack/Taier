---
title: 名称解释
sidebar_label: 名称解释
---

# 名称解释

## 名词解释

### Flink

[`Apache Flink`](https://flink.apache.org/)，一种分布式、高性能、高可用且准确的开源流处理框架。

### UDF

UDF（`User Defined Function`），用户自定义函数，通常适用于 `SQL` 任务。

### Spark

[`Apache Spark`](https://spark.apache.org/)，一种开源的，专为大规模数据处理而设计的快速通用的计算引擎。

### DAG

DAG（`Directed Acyclic Graph`），简称 DAG 。如果一个有向图从任意顶点出发无法经过若干条边回到该点，则这个图是一个有向无环图。任务可以通过上下游以有向无环图的形式组装起来。

### 任务

指用户在 `Taier` 中开发的具体任务， 如 `Spark SQL` 或数据同步任务。

### 实例

实例是根据任务配置的周期实例生成的实例任务。

### 上下游任务

A 任务成功的执行完成后，B、C 任务才可以执行，这种行为称之为 B、C 依赖于 A；在这个例子中，A 是 B、C 的上游任务，B、C 是 A 的下游任务。

### 杀任务

终止某个任务实例的运行。

### 重跑任务

重新运行某个任务。

### 补数据

当任务由于某些原因，修改了业务逻辑时，或发生异常，用户希望对以前的数据重新进行计算，此时会使用**补数据**功能，即手动配置任务的数据源时间段，令其再次运行。

**业务日期**

可理解为业务发生的日期，在 `Taier` 中，业务日期 = 任务的定时执行时间 -1 天。

## 模块介绍

- [taier-common](https://github.com/DTStack/Taier/tree/master/taier-common): 公共类模块
- [taier-dao](https://github.com/DTStack/Taier/tree/master/taier-dao): 数据库相关操作模块
- [taier-data-develop](https://github.com/DTStack/Taier/tree/master/taier-data-develop): 任务开发，运维中心，控制台等逻辑处理等功能模块
- [taier-scheduler](https://github.com/DTStack/Taier/tree/master/taier-scheduler): 处理任务实例生成，实例调度，DAG 图的维护模块
- [taier-worker](https://github.com/DTStack/Taier/tree/master/taier-worker): 任务提交模块
- [taier-datasource](https://github.com/DTStack/Taier/tree/master/taier-datasource): 数据源插件模块
- [taier-ui](https://github.com/DTStack/Taier/tree/master/taier-ui): 前端模块
