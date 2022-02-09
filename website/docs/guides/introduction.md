---
title: 关于 Taiga
sidebar_label: 关于 Taiga
sidebar_position: 1
---

<div align="center">
 <img src="/img/logo.svg" width="20%" height="20%" alt="Taiga Logo" />
 <h1>Taiga</h1>
 <h3>A Distributed dispatching system</h3>
</div>


**Taiga** 名字由来中国古代十大名剑之一 —— **太阿**。

**Taiga** 是一个分布式可视化的DAG任务调度系统。旨在降低**ETL开发**成本、提高大数据平台稳定性，大数据开发人员可以在 **Taiga** 直接进行业务逻辑的开发，而不用关心任务错综复杂的依赖关系与底层的大数据平台的架构实现，将工作的重心更多地聚焦在业务之中。

**Taiga** 脱胎于[袋鼠云](https://www.dtstack.com/) - [数栈](https://www.dtstack.com/dtinsight/) （一站式大数据开发平台），技术实现来源于数栈分布式调度引擎**DAGScheduleX**，**DAGScheduleX**是[数栈](https://www.dtstack.com/dtinsight/) 产品的重要基础设施之一，负责大数据平台所有任务实例的调度运行。

## 功能特征

#### 稳定性
* 单点故障：去中心化的分布式模式
* 高可用方式：`Zookeeper`
* 过载处理：`分布式节点` + `两级存储策略` + `队列机制`。每个节点都可以处理任务调度与提交；任务多时会优先缓存在内存队列，超出可配置的队列最大数量值后会全部落数据库；任务处理以队列方式消费，队列异步从数据库获取可执行实例
* 实战检验：经近百家客户实战检验，部分客户的生产日均调度任务超过`100`万个

#### 易用性
* 支持大数据作业`Spark`、`Flink`、`Hive`、`MR`的调度，
* 支持众多的任务类型，目前支持 Spark SQL、Spark MR、PySpark、Flinkx、Flink MR
:::tip
后续将开源：Python、Shell、Jupyter、Tersorflow、Pytorch、Hadoop MR、Kylin、Odps、SQL(MySQL、PostgreSQL、Hive、SparkSQL、Impala、Oracle、SQLServer、TiDB、greenplum、inceptor、kingbase、presto)、Procedure
:::

* 可视化工作流配置：支持封装工作流、支持单任务运行，不必封装工作流、支持拖拽模式绘制DAG
* DAG监控界面：运维中心、支持集群资源查看，了解当前集群资源的剩余情况、支持对调度队列中的任务批量停止、任务状态、任务类型、重试次数、任务运行机器、可视化变量等关键信息一目了然
* 调度时间配置：可视化配置
* 多集群连接：支持一套调度系统连接多套`Hadoop`集群

#### 多版本引擎
* 支持`Spark` 、`Flink`、`Hive`、`MR`等引擎的多个版本共存，例如可同时支持`Flink1.10`、`Flink1.12`（后续开源）

#### Kerberos支持
* `Flink`、`SparkSQL`、`HiveSQL`

#### 系统参数
* 丰富，支持**3**种时间基准，且可以灵活设置输出格式

#### 扩展性
* 设计之处就考虑分布式模式，目前支持 整体 **Taiga** 水平扩容 和 拆分`Master`/`Worker` 水平扩容方式；
* 调度能力随集群线性增长；  

## 用户界面
![datasource](/img/readme/datasource.png)
![developsync](/img/readme/developsync.png)
![developsparksql](/img/readme/developsparksql.png)
![yunwei](/img/readme/yunwei.png)
![console](/img/readme/console.png)
![component](/img/readme/component.png)

## 快速开始
请参考官方文档: [快速开始](./quickstart/start.md)

## Contributing

Refer to the [CONTRIBUTING](./contributing.md).

## License

**Taiga** is under the Apache 2.0 license. See
the [LICENSE](http://www.apache.org/licenses/LICENSE-2.0) file for details.


## 技术交流
我们使用[钉钉](https://www.dingtalk.com/) 沟通交流，可以搜索群号[**30537511**]或者扫描下面的二维码进入钉钉群
<div align="center"> 
 <img src="/img/readme/ding.jpeg" width="300" />
</div>