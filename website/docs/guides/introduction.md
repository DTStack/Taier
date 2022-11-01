---
title: Taier介绍
sidebar_label: Taier介绍
sidebar_position: 1
---

<div align="center">
 <img src="/Taier/img/logo.svg" width="20%" height="20%" alt="Taier Logo" />
 <h1>Taier</h1>
 <h3>distributed dispatching system</h3>
</div>

> **Taier**  太阿，是中国古代十大名剑之一

**Taier** 是一个开源的分布式 DAG 调度系统，专注不同任务的提交和调度。旨在降低 ETL 开发成本，解决任务之间复杂的依赖关系和提交、调度、运维带来的上手成本

在 **Taier** 上进行 ETL 开发，不用关心任务错综复杂的依赖关系与底层的大数据平台的架构实现，将工作的重心更多地聚焦在业务之中

**Taier** 提供了一个提交、调度、运维、指标信息展示的一站式大数据开发平台

## 功能特征

#### 稳定性

- 单点故障：去中心化的分布式模式
- 高可用方式：`Zookeeper`
- 过载处理：`分布式节点` + `两级存储策略` + `队列机制`。每个节点都可以处理任务调度与提交；任务多时会优先缓存在内存队列，超出可配置的队列最大数量值后会全部落数据库；任务处理以队列方式消费，队列异步从数据库获取可执行实例
- 实战检验：得到数百家企业客户生产环境实战检验

#### 易用性

- 支持大数据作业 `Spark`、`Flink`、`Hive` 的调度，
- 支持众多的任务类型，目前支持 `Spark SQL`、数据同步、实时采集、工作流等任务
- 可视化工作流配置：支持封装工作流、支持单任务运行，不必封装工作流、支持拖拽模式绘制 DAG
- DAG 监控界面：运维中心、支持集群资源查看，了解当前集群资源的剩余情况、支持对调度队列中的任务批量停止、任务状态、任务类型、重试次数、任务运行机器、可视化变量等关键信息一目了然
- 调度时间配置：可视化配置
- 多集群配置：支持一套调度系统对接多 `Hadoop` 集群

#### 多版本引擎

- 支持 `Spark` 、`Flink` 等引擎的多个版本共存

#### Kerberos 支持

- `Spark`、`Flink`

#### 系统参数

- 丰富，支持 **3** 种时间基准，且可以灵活设置输出格式

#### 扩展性

- 设计之处就考虑分布式模式，目前支持整体 `Taier` 水平扩容方式；
- 调度能力随集群线性增长；

## 快速开始
- [官方文档](https://dtstack.github.io/Taier/docs/guides/introduction)
- [docker启动](https://dtstack.github.io/Taier/docs/quickstart/deploy/docker#2-%E4%BD%BF%E7%94%A8docker-compose)
```shell
$ docker-compose up -d
```
- [开发任务](https://dtstack.github.io/Taier/docs/quickstart/start)

## 未来规划

- 任务类型：新增其他类型支持
<details>
  <summary>后续将开源</summary>
  <div>
    <ul>
      <li>SparkMR</li>
      <li>PySpark</li>
      <li>Jupyter</li>
      <li>TersorFlow</li>
      <li>Pytorch</li>
      <li>HadoopMR</li>
      <li>Kylin</li>
      <li>Odps</li>
    </ul>
    <div>SQL 类:</div>
    <ul>
      <li>MySQL</li>
      <li>PostgreSQL</li>
      <li>Impala</li>
      <li>Oracle</li>
      <li>SQLServer</li>
      <li>TiDB</li>
      <li>Greenplum</li>
      <li>Inceptor</li>
      <li>Kingbase</li>
      <li>Trino</li>
    </ul>
  </div>
</details>
- 调度方式：同时支持 `Yarn/K8s`
- 计算引擎：同时支持 `Spark-2.1.x/3.x`、`Flink 1.12`（与 `Flink` 后续版本）
- 部署方式：同时支持 `Scheduler/Worker` 整合与分离部署
- 功能支持：支持交易日历、事件驱动
- 外部系统对接：支持 `Taier` 系统对接外部调度系统（`AZKBAN`、`Control-M`、`DS` 调度）


## 技术交流

我们使用 [钉钉](https://www.dingtalk.com/) 沟通交流，可以搜索群号 [**30537511**] 或者扫描下面的二维码进入钉钉群

<div align="center"> 
 <img src="/Taier/img/readme/ding.jpeg" width="300" />
</div>