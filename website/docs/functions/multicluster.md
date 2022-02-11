---
title: 集群管理
sidebar_label: 集群管理
---

# 控制台

控制台是维护底层资源的管理平台，分为队列管理、资源管理、多集群管理三个模块。

## 多集群管理

多集群管理用于配置任务实例运行所依赖的底层组件，比如资源调度组件 YARN、存储组件 HDFS、计算组件 Flink 等。

将租户绑定到某个集群，该租户提交的任务就会使用该集群下对应的组件。

在实际的数据开发中，我们可以将资源进行合理的分配：不同的集群使用不同的资源，然后让不同环境下的租户绑定到不同的集群，比如测试环境租户绑定到测试集群、预发环境租户绑定预发集群，从而实现对资源的隔离使用。

多集群管理按照公共组件-->资源调度组件-->存储组件-->计算组件的顺序进行配置。

#### 公共组件

公共组件用于配置 SFTP 组件，调度引擎会利用 SFTP 获取上传到指定路径的 kerberos 认证文件。

配置时支持以 .json 格式文件的方式，批量上传 SFTP 配置参数。

![SFTP 配置](/img/readme/sftp.png)

#### 资源调度组件

资源调度组件主要用于配置 YARN 组件，因为某些计算组件需要依赖资源调度组件，比如 Flink 计算组件需要依赖 YARN，所以需要提前配置好资源调度组件。

为了保持 Hadoop 引擎下，YARN 和 HDFS 组件的版本一致性，当切换 YARN 的组件版本，进行保存后，存储组件 HDFS 的版本也将同步变更。

![YARN 配置](/img/readme/yarn.png)


#### 存储组件

存储组件主要用于配置 HDFS 组件，因为某些计算组件需要依赖存储组件，比如 Flink 计算组件需要依赖 HDFS 存储组件，所以需要提前配置好存储组件。

![HDFS 配置](/img/readme/hdfs.png)


#### 计算组件

计算组件主要用于配置 Flink、Spark 等计算引擎。

##### Flink

![Flink 配置](/img/readme/flink.png)

部署模式分为 perjob、session 两种模式。

###### 公共参数

| 参数项                    | 默认值                                   | 说明                                                  | 是否必填 |
| ------------------------- | ---------------------------------------- | ----------------------------------------------------- | -------- |
| clusterMode               | perjob模式为perjob；session模式为session | 任务执行模式：perjob, session                         | 是       |
| flinkJarPath              | /data/insight_plugin/flink110_lib        | flink lib path                                        | 是       |
| remoteFlinkJarPath        | /data/insight_plugin/flink110_lib        | flink lib远程路径                                     | 是       |
| flinkPluginRoot           | /data/insight_plugin                     | flinkStreamSql和flinkx plugins父级本地目录            | 是       |
| remotePluginRootDir       | /data/insight_plugin                     | flinkStreamSql和flinkx plugins父级远程目录            | 是       |
| pluginLoadMode            | shipfile                                 | 插件加载类型                                          | 否       |
| monitorAcceptedApp        | false                                    | 是否监控yarn accepted状态任务                         | 否       |
| yarnAccepterTaskNumber    | 3                                        | 允许yarn accepter任务数量，达到这个值后不允许任务提交 | 否       |
| prometheusHost            |                                          | prometheus地址，平台端使用                            | 是       |
| prometheusPort            | 9090                                     | prometheus，平台端使用                                | 是       |
| classloader.dtstack-cache | true                                     | 是否缓存classloader                                   | 否       |

###### session

| 参数项                      | 默认值        | 说明                                    | 是否必填 |
| --------------------------- | ------------- | --------------------------------------- | -------- |
| checkSubmitJobGraphInterval | 60            | session check间隔（60 * 10s）           | 是       |
| flinkSessionSlotCount       | 10            | flink session允许的最大slot数           | 是       |
| sessionRetryNum             | 5             | session重试次数，达到后会放缓重试的频率 | 是       |
| sessionStartAuto            | true          | 是否允许engine启动flink session         | 是       |
| flinkSess ionName           | flink_session | flink session任务名                     | 是       |
| jobmanager.heap.mb          | 2048          | jobmanager内存大小                      | 是       |
| taskmanager.heap.mb         | 1024          | taskmanager内存大小                     | 是       |

###### Flink 参数项

| 参数项                                              | 默认值                                                       | 说明                         | 是否必填 |
| --------------------------------------------------- | ------------------------------------------------------------ | ---------------------------- | -------- |
| env.java.opts                                       | -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=300m -Dfile.encoding=UTF-8 | jvm参数                      | 否       |
| classloader.resolve-order                           | perjob默认为child-first，session默认为parent-first           | 类加载模式                   | 否       |
| high-availability                                   | ZOOKEEPER                                                    | flink ha类型                 | 是       |
| high-availability.zookeeper.quorum                  | zookeeper地址，当ha选择是zookeeper时必填                     | 是                           |          |
| high-availability.zookeeper.path.root               | /flink110                                                    | ha节点路径                   | 是       |
| high-availability.storageDir                        | hdfs://ns1/dtInsight/flink110/ha                             | ha元数据存储路径             | 是       |
| jobmanager.archive.fs.dir                           | hdfs://ns1/dtInsight/flink110/completed-jobs	任务结束后任务信息存储路径	是 |                              |          |
| metrics.reporter.promgateway.class                  | org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter | 用来推送指标类               | 是       |
| metrics.reporter.promgateway.host                   | promgateway地址                                              | 是                           |          |
| metrics.reporter.promgateway.port                   | 9091                                                         | promgateway端口              | 是       |
| metrics.reporter.promgateway.deleteOnShutdown       | true                                                         | 任务结束后是否删除指标	是 |          |
| metrics.reporter.promgateway.jobName                | 110job	指标任务名                                         | 否                           |          |
| metrics.reporter.promgateway.randomJobNameSuffix    | true                                                         | 是否在任务名上添加随机值     | 是       |
| state.backend                                       | RocksDB                                                      | 状态后端                     | 是       |
| state.backend.incremental                           | true                                                         | 是否开启增量                 | 否       |
| state.checkpoints.dir                               | hdfs://ns1/dtInsight/flink110/checkpoints                    | checkpoint路径地址           | 是       |
| state.checkpoints.num-retained                      | 11                                                           | checkpoint保存个数           | 否       |
| state.savepoints.dir                                | hdfs://ns1/dtInsight/flink110/savepoints                     | savepoint路径                | 是       |
| yarn.application-attempts                           | 3                                                            | 重试次数                     | 否       |
| yarn.application-attempt-failures-validity-interval | 3600000                                                      | 重试窗口时间大小             | 否       |
| akka.ask.timeout                                    | 60 s                                                         |                              | 否       |
| akka.tcp.timeout                                    | 60 s                                                         |                              | 否       |

更多 Flink 参数项详见[官方文档](https://ci.apache.org/projects/flink/flink-docs-release-1.10/ops/config.html)。

##### Spark

![Spark 配置](/img/readme/spark.png)

###### Spark 参数项

| 参数项                                 | 默认值                      | 说明                                   | 是否必填 |
| -------------------------------------- | --------------------------- | -------------------------------------- | -------- |
| spark.driver.extraJavaOptions          | -Dfile.encoding=UTF-8       | driver的jvm参数                        | 否       |
| spark.executor.extraJavaOptions        | -Dfile.encoding=UTF-8       | executor的jvm参数                      | 否       |
| spark.eventLog.compress                | false                       | 是否压缩日志                           | 否       |
| spark.eventLog.dir                     | hdfs://ns1/tmp/logs         | spark日志存放路径                      | 否       |
| spark.eventLog.enabled                 | true                        | 是否记录 Spark 日志                    | 否       |
| spark.executor.cores                   | 1                           | 每个执行程序上使用的内核数             | 是       |
| spark.executor.heartbeatInterval       | 10s                         | 每个执行程序对驱动程序的心跳之间的间隔 | 是       |
| spark.executor.instances               | 1                           | 启动执行程序进程的实例数               | 是       |
| spark.executor.memory                  | 1g                          | 每个执行程序进程使用的内存量           | 是       |
| spark.network.timeout                  | 600s                        | 所有网络交互的默认超时时长             | 是       |
| spark.rpc.askTimeout                   | 600s                        | RPC 请求操作在超时之前等待的持续时间   | 是       |
| spark.submit.deployMode                | cluster                     | spark任务提交模式                      | 是       |
| spark.yarn.appMasterEnv.PYSPARK_PYTHON | /data/anaconda3/bin/python3 | python环境变量路径                     | 否       |
| spark.yarn.maxAppAttempts              | 4                           | 提交申请的最大尝试次数                 | 是       |

更多 Spark 参数项详见[官方文档](https://spark.apache.org/docs/2.1.3/configuration.html)。

###### 自定义参数

| 参数项                 | 默认值                                                       | 说明                                       | 是否必填 |
| ---------------------- | ------------------------------------------------------------ | ------------------------------------------ | -------- |
| sparkPythonExtLibPath  | hdfs://ns1/dtInsight/spark240/pythons/pyspark.zip,hdfs://ns1/dtInsight/spark240/pythons/py4j-0.10.7-src.zip | pyspark.zip和py4j-0.10.7-src.zip路径       | 是       |
| sparkSqlProxyPath      | hdfs://ns1/dtInsight/spark240/client/spark-sql-proxy.jar     | spark-sql-proxy.jar路径，用于执行spark sql | 是       |
| sparkYarnArchive       | hdfs://ns1/dtInsight/spark240/jars                           | spark jars路径                             | 是       |
| yarnAccepterTaskNumber | 3                                                            | 允许的accepter任务数量                     | 否       |

