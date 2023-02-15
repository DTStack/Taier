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
    <a href="https://dtstack.github.io/Taier/">Office Website</a> |
    <a href="https://dtstack.github.io/Taier/docs/guides/introduction/">Document</a>
  </p>
</p>

[中文](./README_zh-CN.md) | [English](./README.md) 

## Introduction

> **Taier** , spelling in chinese is 太阿, is one of the ancient chinese celebrated swords.

**Taier** is distributed dispatching system that focus on different tasks submitted and scheduled. It's aimed at reducing the **ETL**'s cost, making the complex dependencies between tasks clearly and reducing the labor cost about submitting, scheduling and O&M.

It's unnecessary to concern about the complex dependencies between tasks and the underlying architecture about the big data platform at **Taier**, so that you can pay more attention into business.

**Taier** provide an one-stop big data platform for submitting tasks, scheduling tasks, O&M, presentation about indicators.

The core features for Taier are as follows:


- Easy to distributed extend
- Visualization config for DAG
- With an IDE development platform designed for big-data users
- Supports to develop your own plugins
- Multiply task modes including guide mode and script mode
- Supports to the dependencies between upstream/downstream tasks
- Supports batch and stream tasks
- Integrates various different versions of Hadoop
- Easy to integrate Flink Standalone
- Completely safe and non-intrusive to the cluster's environment
- Isolation by tenants and clusters
- Supports kerberos authentication
- Different versions for tasks
- Supports user-defined parameters for task
- Real-time monitoring about cluster's resources
- Real-time presented about data indicators
- Restrict the task's resource

## Architecture

![architecture](/website/static/img/readme/taier-architecture.png)

## Quick start

#### [In Docker](https://dtstack.github.io/Taier/docs/quickstart/deploy/docker#2-%E4%BD%BF%E7%94%A8docker-compose)

```shell
$ wget https://raw.githubusercontent.com/DTStack/Taier/master/docker-compose.yml
$ docker-compose up -d
```

#### [Online Preview](http://taier.dtstack.cn/)

#### [Develop Tasks](https://dtstack.github.io/Taier/docs/quickstart/start)

![main](/website/static/img/readme/main.png)

## Tasks

|       Tasks       |                                     Documentation                                     |
| :---------------: | :-----------------------------------------------------------------------------------: |
|     Work Flow     |       [Documentation](https://dtstack.github.io/Taier/docs/functions/task/workflow)   |
|     Data Sync     |       [Documentation](https://dtstack.github.io/Taier/docs/functions/task/sync)       |
| Data Acquisition  | [Documentation](https://dtstack.github.io/Taier/docs/functions/task/data-acquisition) |
|       Flink       |      [Documentation](https://dtstack.github.io/Taier/docs/functions/task/flink)       |
|       Shell       |      [Documentation](https://dtstack.github.io/Taier/docs/functions/task/shell)       |
|      Python       |      [Documentation](https://dtstack.github.io/Taier/docs/functions/task/python)      |
|     Spark SQL     |    [Documentation](https://dtstack.github.io/Taier/docs/functions/task/spark-sql)     |
|     Hive SQL      |     [Documentation](https://dtstack.github.io/Taier/docs/functions/task/hive-sql)     |
|     Flink SQL     |    [Documentation](https://dtstack.github.io/Taier/docs/functions/task/flink-sql)     |
|   OceanBase SQL   |  [Documentation](https://dtstack.github.io/Taier/docs/functions/task/oceanbase-sql)   |
|  ClickHouse SQL   |  [Documentation](https://dtstack.github.io/Taier/docs/functions/task/clickhouse-sql)  |
|     Doris SQL     |    [Documentation](https://dtstack.github.io/Taier/docs/functions/task/doris-sql)     |
|      TiDB SQL     |      [Documentation](https://dtstack.github.io/Taier/docs/functions/task/tidb-sql)    |
|      MySQL SQL    |      [Documentation](https://dtstack.github.io/Taier/docs/functions/task/mysql-sql)   |
|      Vertica SQL  |      [Documentation](https://dtstack.github.io/Taier/docs/functions/task/vertica-sql) |
|      Postgre SQL  |      [Documentation](https://dtstack.github.io/Taier/docs/functions/task/postgre-sql) |
|     SqlServer SQL |     [Documentation](https://dtstack.github.io/Taier/docs/functions/task/sqlserver-sql)|
|   Greenplum SQL   |     [Documentation](https://dtstack.github.io/Taier/docs/functions/task/greenplum-sql)|
|   MaxCompute SQL  |    [Documentation](https://dtstack.github.io/Taier/docs/functions/task/maxcompute-sql)|
|     GaussDB SQL   |      [Documentation](https://dtstack.github.io/Taier/docs/functions/task/guassdb-sql) |
| User-defined Task |           [Documentation](https://dtstack.github.io/Taier/docs/expand/task)           |

## Questions

For questions, bugs and supports please open an [issue](https://github.com/DTStack/Taier/issues/new/choose), we'll reply you in time.

## Stay in touch

- Slack https://join.slack.com/t/slack-p437975/shared_invite/zt-1iw5x1fw7-A6rVolqjP2z8V09~WwFUiA

## Contribution

Please make sure to read the [Contributing Guide](https://dtstack.github.io/Taier/docs/contributing) before making a pull request.

## Contributor

<a href="https://github.com/Dtstack/Taier/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Dtstack/Taier" />
</a>

## License

**Taier** is under the Apache 2.0 license. See the [LICENSE](http://www.apache.org/licenses/LICENSE-2.0) file for
details.
