<div align="center">
         <a href="https://dtstack.github.io/Taier/" target="_blank" rel="noopener noreferrer">
           <img src="website/static/img/logo.svg" width="20%" height="20%" alt="Taier Logo" />
        </a>
 <h1>Taier</h1>
 <h3>A distributed dispatching system</h3>
</div>


<p align="center">
  <img src="https://img.shields.io/github/release/Dtstack/Taier.svg">
  <img src="https://img.shields.io/github/stars/Dtstack/Taier">
  <img src="https://img.shields.io/github/forks/Dtstack/Taier">
  <a href="https://www.apache.org/licenses/LICENSE-2.0.html">
   <img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg">
  </a>
  <p align="center">
    <a href="https://dtstack.github.io/Taier/">官网</a> |
    <a href="https://dtstack.github.io/Taier/docs/guides/introduction/">官方文档</a>
  </p>
</p>

[中文](./README_zh-CN.md) | [English](./README.md)

## 介绍

> **Taier**  太阿，是中国古代十大名剑之一

**Taier** 是一个开源的分布式 DAG 调度系统，专注不同任务的提交和调度。旨在降低 **ETL** 开发成本，解决任务之间复杂的依赖关系和提交、调度、运维带来的上手成本

在 **Taier** 上进行 ETL 开发，不用关心任务错综复杂的依赖关系与底层的大数据平台的架构实现，将工作的重心更多地聚焦在业务之中

**Taier** 提供了一个提交、调度、运维、指标信息展示的一站式大数据开发平台

## 功能

核心功能如下:

- 分布式扩展
- 可视化 DAG 配置
- IDE 式开发平台
- 自定义扩展任务插件
- 向导、脚本多种模式
- 上下游依赖调度
- 支持实时、离线任务
- 支持对接不同版本的 Hadoop
- 支持Flink Standalone
- 对集群环境 0 侵入
- 多租户多集群隔离
- 支持 Kerberos 认证
- 任务多版本支持
- 自定义参数替换
- 集群资源实时监控
- 数据指标实时获取
- 任务资源限制

## 架构

![architecture](/website/static/img/readme/taier-architecture.png)

## 快速开始
#### [docker 启动](https://dtstack.github.io/Taier/docs/quickstart/deploy/docker#2-%E4%BD%BF%E7%94%A8docker-compose) 

```shell
$ wget https://github.com/DTStack/Taier/blob/master/docker-compose.yml
$ docker-compose up -d
```

#### [在线预览地址](http://taier.dtstack.cn/)

#### [开发任务](https://dtstack.github.io/Taier/docs/quickstart/start)
![main](/website/static/img/readme/main.png)

## 任务类型

|       Tasks       |                                     Documentation                                     |
| :---------------: | :-----------------------------------------------------------------------------------: |
|        工作流      |       [文档](https://dtstack.github.io/Taier/docs/functions/task/workflow)   |
|      数据同步      |       [文档](https://dtstack.github.io/Taier/docs/functions/task/sync)       |
|      实时采集      | [文档](https://dtstack.github.io/Taier/docs/functions/task/data-acquisition) |
|       Flink       |      [文档](https://dtstack.github.io/Taier/docs/functions/task/flink)       |
|       Shell       |      [文档](https://dtstack.github.io/Taier/docs/functions/task/shell)       |
|      Python       |      [文档](https://dtstack.github.io/Taier/docs/functions/task/python)      |
|     Spark SQL     |    [文档](https://dtstack.github.io/Taier/docs/functions/task/spark-sql)     |
|     Hive SQL      |     [文档](https://dtstack.github.io/Taier/docs/functions/task/hive-sql)     |
|     Flink SQL     |    [文档](https://dtstack.github.io/Taier/docs/functions/task/flink-sql)     |
|   OceanBase SQL   |  [文档](https://dtstack.github.io/Taier/docs/functions/task/oceanbase-sql)   |
|  ClickHouse SQL   |  [文档](https://dtstack.github.io/Taier/docs/functions/task/clickhouse-sql)  |
|     Doris SQL     |    [文档](https://dtstack.github.io/Taier/docs/functions/task/doris-sql)     |
|      TiDB SQL     |      [文档](https://dtstack.github.io/Taier/docs/functions/task/tidb-sql)    |
|      MySQL SQL    |      [文档](https://dtstack.github.io/Taier/docs/functions/task/mysql-sql)   |
|      Vertica SQL  |      [文档](https://dtstack.github.io/Taier/docs/functions/task/vertica-sql) |
|      Postgre SQL  |      [文档](https://dtstack.github.io/Taier/docs/functions/task/postgre-sql) |
|     SqlServer SQL |     [文档](https://dtstack.github.io/Taier/docs/functions/task/sqlserver-sql)|
|   Greenplum SQL   |     [文档](https://dtstack.github.io/Taier/docs/functions/task/greenplum-sql)|
|   MaxCompute SQL  |    [文档](https://dtstack.github.io/Taier/docs/functions/task/maxcompute-sql)|
|     GaussDB SQL   |      [文档](https://dtstack.github.io/Taier/docs/functions/task/guassdb-sql) |
|      自定义扩展     |           [文档](https://dtstack.github.io/Taier/docs/expand/task)           |

## 问题反馈
[常见问题](https://dtstack.github.io/Taier/docs/quickstart/faq) 在使用上有遇到 bug 或者优化点，强烈建议你提 [issue](https://github.com/DTStack/Taier/issues/new/choose) 我们将及时修复


## 联系我们
- Slack https://join.slack.com/t/slack-p437975/shared_invite/zt-1iw5x1fw7-A6rVolqjP2z8V09~WwFUiA


## 贡献代码

我该如何贡献？

[参考文档](https://dtstack.github.io/Taier/docs/contributing)

## 贡献者

<a href="https://github.com/Dtstack/Taier/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Dtstack/Taier" />
</a>

## License

**Taier** is under the Apache 2.0 license. See the [LICENSE](http://www.apache.org/licenses/LICENSE-2.0) file for
details.
