---
title: 关于 Taier
sidebar_label: 关于 Taier
sidebar_position: 1
---

<div align="center">
 <img src="/Taier/img/logo.svg" width="20%" height="20%" alt="Taier Logo" />
 <h1>Taier</h1>
 <h3>A Distributed dispatching system</h3>
</div>

**Taier** 名字由来中国古代十大名剑之一 —— **太阿**。

**Taier** 是一个分布式可视化的 DAG 任务调度系统。旨在降低 **ETL 开发**成本、提高大数据平台稳定性，大数据开发人员可以在 **Taier** 直接进行业务逻辑的开发，而不用关心任务错综复杂的依赖关系与底层的大数据平台的架构实现，将工作的重心更多地聚焦在业务之中。

**Taier** 脱胎于 [袋鼠云](https://www.dtstack.com/) - [数栈](https://www.dtstack.com/dtinsight/) （一站式大数据开发平台），技术实现来源于数栈分布式调度引擎 **DAGScheduleX**。

**DAGScheduleX** 是 [数栈](https://www.dtstack.com/dtinsight/) 产品的重要基础设施之一，负责大数据平台所有任务实例的调度运行。

## 功能特征

#### 稳定性

- 单点故障：去中心化的分布式模式
- 高可用方式：`Zookeeper`
- 过载处理：`分布式节点` + `两级存储策略` + `队列机制`。每个节点都可以处理任务调度与提交；任务多时会优先缓存在内存队列，超出可配置的队列最大数量值后会全部落数据库；任务处理以队列方式消费，队列异步从数据库获取可执行实例
- 实战检验：得到数百家企业客户生产环境实战检验

#### 易用性

- 支持大数据作业 `Spark`、`Flink`、`Hive` 的调度，
- 支持众多的任务类型，目前支持 `Spark SQL`、数据同步、实时采集、工作流等任务

<details>
  <summary>后续将开源</summary>
  <div>
    <ul>
      <li>SparkMR</li>
      <li>PySpark</li>
      <li>FlinkMR</li>
      <li>Python</li>
      <li>Shell</li>
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
      <li>greenplum</li>
      <li>inceptor</li>
      <li>kingbase</li>
      <li>presto</li>
    </ul>
  </div>
</details>

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

## 主要界面

![add-source](/img/readme/add-source.png)
![cluster](/img/readme/cluster.png)
![web](/img/readme/main.png)
![sync](/img/readme/sync.png)
![maintenance](/img/readme/maintenance.png)

## 快速开始

请参考官方文档: [快速上手](./quickstart/start.md)

## 未来规划

- 任务类型：新增其他类型支持
<details>
  <summary>后续将开源</summary>
  <div>
    <ul>
      <li>SparkMR</li>
      <li>PySpark</li>
      <li>FlinkMR</li>
      <li>Python</li>
      <li>Shell</li>
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
      <li>greenplum</li>
      <li>inceptor</li>
      <li>kingbase</li>
      <li>presto</li>
    </ul>
  </div>
</details>
- 调度方式：同时支持 `Yarn/K8s`
- 计算引擎：同时支持 `Spark-2.1.x/2.4.x`、`Flink 1.12`（与 `Flink` 后续版本）
- 部署方式：同时支持 `Scheduler/Worker` 整合与分离部署
- 功能支持：支持交易日历、事件驱动
- 外部系统对接：支持 `Taier` 系统对接外部调度系统（`AZKBAN`、`Control-M`、`DS` 调度）

## 贡献

请参考如何 [贡献](./contributing.md).

## License

**Taier** is under the Apache 2.0 license. See
the [LICENSE](http://www.apache.org/licenses/LICENSE-2.0) file for details.

## 技术交流

我们使用 [钉钉](https://www.dingtalk.com/) 沟通交流，可以搜索群号 [**30537511**] 或者扫描下面的二维码进入钉钉群

<div align="center"> 
 <img src="/Taier/img/readme/ding.jpeg" width="300" />
</div>
