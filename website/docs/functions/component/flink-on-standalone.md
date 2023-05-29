---
title: Flink On Standalone  
sidebar_label: Flink On Standalone
---

## 启动Flink Standalone环境

**1、下载 flink**

* flink官网下载 [release 1.12.7](https://flink.apache.org/downloads.html)

**2、chunjun的插件**

* 编译1.12版本的chunjun [master](https://github.com/DTStack/chunjun)插件，将生成的chunjun-dist 拷贝至 $FLINK_HOME/lib

:::tip 
chunjun 有些依赖包依赖是provided，需要将缺省的jar放到$FLINK_HOME/lib  
例如：在chunjun-core最新版本中将logback的依赖改为了provided, 需要将以下logback包放到$FLINK_HOME/lib logback-core-1.2.11.jar
logback-classic-1.2.11.jar
:::

**3、flink1.12 standalone服务参数**
所有节点都需要配置

```
# 类加载配置
classloader.resolve-order: parent-first
classloader.check-leaked-classloader: false

# 高可用配置(单机 None)
high-availability: zookeeper
high-availability.zookeeper.quorum: kudu1:2181,kudu2:2181,kudu3:2181
high-availability.zookeeper.path.root: /flink112
high-availability.storageDir: hdfs://ns1/dtInsight/flink112/ha
high-availability.cluster-id: /standalone_ha

# 指标配置
metrics.reporter.promgateway.class: org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter
metrics.reporter.promgateway.host: 172.16.23.25
metrics.reporter.promgateway.port: 9091
metrics.reporter.promgateway.jobName: flink112
metrics.reporter.promgateway.randomJobNameSuffix: true
metrics.reporter.promgateway.deleteOnShutdown: false
```


flink standalone节点lib目录结构和文件，
:::caution
lib需要包含`chunjun-dist源码包`
:::

```shell
lib/
├── chunjun-dist
│   ├── chunjun-core.jar
│   ├── connector
│   ├── ddl-plugins
│   ├── dirty-data-collector
│   ├── docker-build
│   ├── formats
│   ├── metrics
│   └── restore-plugins
│       └── mysql
│           └── chunjun-restore-mysql.jar
├── flink-csv-1.12.7.jar
├── flink-dist_2.11-1.12.7.jar
├── flink-json-1.12.7.jar
├── flink-shaded-zookeeper-3.4.14.jar
├── flink-table_2.11-1.12.7.jar
├── flink-table-blink_2.11-1.12.7.jar
├── log4j-1.2-api-2.16.0.jar
├── log4j-api-2.16.0.jar
├── log4j-core-2.16.0.jar
├── log4j-slf4j-impl-2.16.0.jar
├── logback-classic-1.2.11.jar
└── logback-core-1.2.11.jar


```


## Standalone 控制台参数

| 参数名 | 含义 | 是否必填 * 为必填 | 默认值 |
| --- | --- | --- | --- |
| clusterMode               |flink部署模式          | * | standalone |
| flinkLibDir               | flink libs的路径      | * | /data/flink112_lib |
| chunjunDistDir            | chunjun 插件地址       | * | /data/chunjun/chunjun-dist |
| remoteChunjunDistDir       | chunjun 插件远端地址    | * | /data/chunjun/chunjun-dist |
| pluginLoadMode            | 加载文件的方式          | * | classpath |
| jobmanager.rpc.address     | jobmanager rpc地址     | * |  |
| jobmanager.rpc.port        | jobmanager rpc端口号   | *  |  |
| rest.port                 | ui端口号                 |  * |  |
| high-availability          | 高可用服务类型            |   | NONE |
| high-availability.zookeeper.quorum     | zookeeper集群地址     |  |  |
| high-availability.zookeeper.path.root  | flink存储状态在zookeeper的根节点路径         |  |  |
| high-availability.storageDir          | flink高可用模式下存储元数据的文件系统路径(URI) |  |  |
| high-availability.cluster-id          | flink集群的id， 用于区分多个flink集群         |  |  |
| prometheusHost  | prometheus地址 | * |  |
| prometheusPort  | prometheus端口 | * |  |
| state.backend   | 状态后端       |   | jobmanager |

:::caution 
flinkLibDir需要和$FLINK_HOME/lib文件一致  
chunjunDistDir需要和$FLINK_HOME/lib/下chunjun插件包文件一致
:::

:::tip 
flink on standalone 离线任务需要手动将`环境参数`flinkTaskRunMode设置为`standalone`模式
:::



