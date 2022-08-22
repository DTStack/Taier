<div align="center">
         <a href="https://dtstack.github.io/Taier/" target="_blank" rel="noopener noreferrer">
           <img src="website/static/img/logo.svg" width="20%" height="20%" alt="Taier Logo" />
        </a>
 <h1>Taier</h1>
 <h3>distributed dispatching system</h3>
</div>


<p align="center">
  <img src="https://img.shields.io/github/release/Dtstack/Taier.svg">
  <img src="https://img.shields.io/github/stars/Dtstack/Taier">
  <img src="https://img.shields.io/github/forks/Dtstack/Taier">
  <a href="https://www.apache.org/licenses/LICENSE-2.0.html">
   <img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg"></a>
</p>

## 介绍
> **Taier**  太阿，是中国古代十大名剑之一

**Taier**是一个开源的分布式DAG调度系统，专注不同任务的提交和调度。旨在降低**ETL**开发成本，解决任务之间复杂的依赖关系和提交、调度、运维带来的上手成本  

在**Taier**上进行ETL开发，不用关心任务错综复杂的依赖关系与底层的大数据平台的架构实现，将工作的重心更多地聚焦在业务之中  

**Taier**提供了一个提交、调度、运维、指标信息展示的一站式大数据开发平台

## 功能
核心功能如下:
- **分布式扩展**
- **可视化DAG配置**
- **IDE式开发平台**
- **自定义扩展任务插件**
- **向导、脚本多种模式**
- **上下游依赖调度**
- **支持实时、离线任务**
- **支持对接不同版本的hadoop**
- **对集群环境0侵入**
- **多租户多集群隔离**
- **支持kerberos认证**
- **任务多版本支持**
- **自定义参数替换**
- **集群资源实时监控**
- **数据指标实时获取**
- **任务资源限制**

![architecture](/website/static/img/readme/taier-architecture.png)


## 快速开始
- [官方文档](https://dtstack.github.io/Taier/docs/guides/introduction)  
- [docker启动](https://dtstack.github.io/Taier/docs/quickstart/deploy/docker#2-%E4%BD%BF%E7%94%A8docker-compose)
```shell
$ docker-compose up -d
```
- [开发任务](https://dtstack.github.io/Taier/docs/quickstart/start)
![main](/website/static/img/readme/main.png)
## 任务类型

| 任务类型      | 文档说明 |
| :---:        |    :----:   |
| 数据同步      | [文档](https://dtstack.github.io/Taier/docs/functions/task/sync)|
| 实时采集      | [文档](https://dtstack.github.io/Taier/docs/functions/task/data-acquisition)     |
| Flink SQL   | [文档](https://dtstack.github.io/Taier/docs/functions/task/flink-sql)      |
| Spark SQL   | [文档](https://dtstack.github.io/Taier/docs/functions/task/spark-sql)     |
| Hive SQL   |[文档](https://dtstack.github.io/Taier/docs/functions/task/hive-sql)     |
| OceanBase SQL   |[文档](https://dtstack.github.io/Taier/docs/functions/task/oceanbase-sql)   |
| 自定义扩展   |[文档](https://dtstack.github.io/Taier/docs/expand/task)   |



## 问题反馈
在使用上有遇到bug或者优化点，强烈建议你提[issue](https://github.com/DTStack/Taier/issues/new/choose) 我们将及时修复

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
