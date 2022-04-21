---
title: 集群配置
sidebar_label: 集群配置
---

# 控制台

控制台是维护底层资源的管理平台，分为队列管理、资源管理、多集群管理三个模块

## 多集群管理

多集群管理用于配置任务实例运行所依赖的底层组件，比如资源调度组件 YARN、存储组件 HDFS、计算组件 Flink 等

将租户绑定到某个集群，该租户提交的任务就会使用该集群下对应的组件
:::tip
在实际的数据开发中，我们可以将资源进行合理的分配：不同的集群使用不同的资源，然后让不同环境下的租户绑定到不同的集群，比如测试环境租户绑定到测试集群、预发环境租户绑定预发集群，从而实现对资源的隔离使用
:::

:::info
多集群管理按照公共组件-->资源调度组件-->存储组件-->计算组件的顺序进行配置
:::

### 公共组件

公共组件用于配置 SFTP 组件，相关资源、配置文件上传下载都会使用对应的sftp组件来操作

:::tip
sftp组件用于保存yarn组件中的配置文件信息 以及kerberos文件信息 任意一台机器即可 但需要保证计算节点和Taier网络能访问
:::

![SFTP 配置](/img/readme/sftp.png)

### 资源调度组件

资源调度组件主要用于配置 YARN 组件，因为某些计算组件需要依赖资源调度组件，比如 Flink 计算组件需要依赖 YARN，所以需要提前配置好资源调度组件

![YARN 配置](/img/readme/yarn.png)
:::tip
不同厂商的hadoop集群提交任务会依赖不同的参数，可以在适配hadoop集群的时候通过自定义参数来动态调整

这里默认以Apache Hadoop2 为例 如果没有对应hadoop版本 可以使用Apache Hadoop2 通过采用适配集群的方式来提交任务
:::

### 存储组件

存储组件主要用于配置 HDFS 组件，因为某些计算组件需要依赖存储组件，比如 Flink 计算组件需要依赖 HDFS 存储组件，所以需要提前配置好存储组件

![HDFS 配置](/img/readme/hdfs.png)
:::tip
为了保持 Hadoop 引擎下，YARN 和 HDFS 组件的版本一致性，当切换 YARN 的组件版本，进行保存后，存储组件 HDFS 的版本也将同步变更
:::

### 计算组件

计算组件主要用于配置 Flink、Spark 等计算引擎

#### Flink

![Flink 配置](/img/readme/flink.png)

部署模式分为 perjob、session 两种模式

##### 公共参数

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
| prometheusHost            |                                          | prometheus地址，获取数据同步指标使用                           | 是       |
| prometheusPort            | 9090                                     | prometheus，获取数据同步指标使用                                | 是       |
| classloader.dtstack-cache | true                                     | 是否缓存classloader                                   | 否       |

##### session

| 参数项                      | 默认值        | 说明                                    | 是否必填 |
| --------------------------- | ------------- | --------------------------------------- | -------- |
| checkSubmitJobGraphInterval | 60            | session check间隔（60 * 10s）           | 是       |
| flinkSessionSlotCount       | 10            | flink session允许的最大slot数           | 是       |
| sessionRetryNum             | 5             | session重试次数，达到后会放缓重试的频率 | 是       |
| sessionStartAuto            | true          | 是否允许Taier启动flink session         | 是       |
| flinkSessionName           | flink_session | flink session任务名                     | 是       |
| jobmanager.heap.mb          | 2048          | jobmanager内存大小                      | 是       |
| taskmanager.heap.mb         | 1024          | taskmanager内存大小                     | 是       |

##### Flink 参数项

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

更多 Flink 参数项详见[官方文档](https://ci.apache.org/projects/flink/flink-docs-release-1.10/ops/config.html)

prejob和session 都依赖chunjun的插件包和flink的lib包  

**flinkJarPath**配置的是flink 的lib目录  
如:flinkJarPath = /opt/dtstack/flink110_lib  
/opt/dtstack/flink110_lib 目录结构为:
```shell
├── flink-dist_2.11-1.10.0.jar
├── flink-metrics-prometheus-1.10.0.jar
├── flink-shaded-hadoop-2-uber-2.7.5-10.0.jar
├── flink-streaming-java_2.11-1.10.0.jar
├── flink-table_2.11-1.10.0.jar
├── flink-table-blink_2.11-1.10.0.jar
└── log4j-1.2.17.jar
```
**flinkPluginRoot**配置的是chunjun的插件包目录  
如:flinkPluginRoot = /opt/dtstack/110_flinkplugin  
/opt/dtstack/110_flinkplugin 目录结构为:
```shell
└── syncplugin
    ├── adbpostgresqlreader
    │   └── flinkx-adbpostgresql-reader-feat_1.10_4.3.x_metadata.jar
    ├── adbpostgresqlwriter
    │   └── flinkx-adbpostgresql-writer-feat_1.10_4.3.x_metadata.jar
    ├── binlogreader
    │   └── flinkx-binlog-reader-feat_1.10_4.3.x_metadata.jar
    ├── carbondatareader
    │   └── flinkx-carbondata-reader.jar
    ├── carbondatawriter
    │   └── flinkx-carbondata-writer.jar
    ├── cassandrareader
    │   └── flinkx-cassandra-reader-feat_1.10_4.3.x_metadata.jar
    ├── cassandrawriter
    │   └── flinkx-cassandra-writer-feat_1.10_4.3.x_metadata.jar
    ├── clickhousereader
    │   └── flinkx-clickhouse-reader-feat_1.10_4.3.x_metadata.jar
    ├── clickhousewriter
    │   └── flinkx-clickhouse-writer-feat_1.10_4.3.x_metadata.jar
    ├── common
    │   ├── flinkx-rdb-core-feat_1.10_4.3.x_metadata.jar
    │   ├── flinkx-rdb-reader-feat_1.10_4.3.x_metadata.jar
    │   └── flinkx-rdb-writer-feat_1.10_4.3.x_metadata.jar
    ├── db2reader
    │   └── flinkx-db2-reader-feat_1.10_4.3.x_metadata.jar
    ├── db2writer
    │   └── flinkx-db2-writer-feat_1.10_4.3.x_metadata.jar
    ├── dmreader
    │   └── flinkx-dm-reader-feat_1.10_4.3.x_metadata.jar
    ├── dmwriter
    │   └── flinkx-dm-writer-feat_1.10_4.3.x_metadata.jar
    ├── doriswriter
    │   └── flinkx-doris-writer-feat_1.10_4.3.x_metadata.jar
    ├── emqxreader
    │   └── flinkx-emqx-reader-feat_1.10_4.3.x_metadata.jar
    ├── emqxwriter
    │   └── flinkx-emqx-writer-feat_1.10_4.3.x_metadata.jar
    ├── esreader
    │   └── flinkx-es-reader-feat_1.10_4.3.x_metadata.jar
    ├── eswriter
    │   └── flinkx-es-writer-feat_1.10_4.3.x_metadata.jar
    ├── ftpreader
    │   └── flinkx-ftp-reader-feat_1.10_4.3.x_metadata.jar
    ├── ftpwriter
    │   └── flinkx-ftp-writer-feat_1.10_4.3.x_metadata.jar
    ├── gbasereader
    │   └── flinkx-gbase-reader-feat_1.10_4.3.x_metadata.jar
    ├── gbasewriter
    │   └── flinkx-gbase-writer-feat_1.10_4.3.x_metadata.jar
    ├── greenplumreader
    │   └── flinkx-greenplum-reader-feat_1.10_4.3.x_metadata.jar
    ├── greenplumwriter
    │   └── flinkx-greenplum-writer-feat_1.10_4.3.x_metadata.jar
    ├── hbasereader
    │   └── flinkx-hbase-reader-feat_1.10_4.3.x_metadata.jar
    ├── hbasewriter
    │   └── flinkx-hbase-writer-feat_1.10_4.3.x_metadata.jar
    ├── hdfsreader
    │   └── flinkx-hdfs-reader-feat_1.10_4.3.x_metadata.jar
    ├── hdfswriter
    │   └── flinkx-hdfs-writer-feat_1.10_4.3.x_metadata.jar
    ├── hivewriter
    │   └── flinkx-hive-writer-feat_1.10_4.3.x_metadata.jar
    ├── inceptorreader
    │   └── flinkx-inceptor-reader-feat_1.10_4.3.x_metadata.jar
    ├── inceptorwriter
    │   └── flinkx-inceptor-writer-feat_1.10_4.3.x_metadata.jar
    ├── influxdbreader
    │   └── flinkx-influxdb-reader-feat_1.10_4.3.x_metadata.jar
    ├── kafka09reader
    │   └── flinkx-kafka09-reader-feat_1.10_4.3.x_metadata.jar
    ├── kafka09writer
    │   └── flinkx-kafka09-writer-feat_1.10_4.3.x_metadata.jar
    ├── kafka10reader
    │   └── flinkx-kafka10-reader-feat_1.10_4.3.x_metadata.jar
    ├── kafka10writer
    │   └── flinkx-kafka10-writer-feat_1.10_4.3.x_metadata.jar
    ├── kafka11reader
    │   └── flinkx-kafka11-reader-feat_1.10_4.3.x_metadata.jar
    ├── kafka11writer
    │   └── flinkx-kafka11-writer-feat_1.10_4.3.x_metadata.jar
    ├── kafkareader
    │   └── flinkx-kafka-reader-feat_1.10_4.3.x_metadata.jar
    ├── kafkawriter
    │   └── flinkx-kafka-writer-feat_1.10_4.3.x_metadata.jar
    ├── kingbasereader
    │   └── flinkx-kingbase-reader-feat_1.10_4.3.x_metadata.jar
    ├── kingbasewriter
    │   └── flinkx-kingbase-writer-feat_1.10_4.3.x_metadata.jar
    ├── kudureader
    │   └── flinkx-kudu-reader-feat_1.10_4.3.x_metadata.jar
    ├── kuduwriter
    │   └── flinkx-kudu-writer-feat_1.10_4.3.x_metadata.jar
    ├── metadatahbasereader
    │   └── flinkx-metadata-hbase-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadatahive1reader
    │   └── flinkx-metadata-hive1-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadatahive2reader
    │   └── flinkx-metadata-hive2-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadatahivecdcreader
    │   └── flinkx-metadata-hivecdc-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadatakafkareader
    │   └── flinkx-metadata-kafka-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadatamysqlreader
    │   └── flinkx-metadata-mysql-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadataoraclereader
    │   └── flinkx-metadata-oracle-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadataphoenix5reader
    │   └── flinkx-metadata-phoenix5-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadatasparkthriftreader
    │   └── flinkx-metadata-sparkthrift-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadatasqlserverreader
    │   └── flinkx-metadata-sqlserver-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadatatidbreader
    │   └── flinkx-metadata-tidb-reader-feat_1.10_4.3.x_metadata.jar
    ├── metadataverticareader
    │   └── flinkx-metadata-vertica-reader-feat_1.10_4.3.x_metadata.jar
    ├── mongodbreader
    │   └── flinkx-mongodb-reader-feat_1.10_4.3.x_metadata.jar
    ├── mongodbwriter
    │   └── flinkx-mongodb-writer-feat_1.10_4.3.x_metadata.jar
    ├── mysqlcdcreader
    │   └── flinkx-cdc-reader-feat_1.10_4.3.x_metadata.jar
    ├── mysqldreader
    │   └── flinkx-mysql-dreader-feat_1.10_4.3.x_metadata.jar
    ├── mysqlreader
    │   └── flinkx-mysql-reader-feat_1.10_4.3.x_metadata.jar
    ├── mysqlwriter
    │   └── flinkx-mysql-writer-feat_1.10_4.3.x_metadata.jar
    ├── odpsreader
    │   └── flinkx-odps-reader-feat_1.10_4.3.x_metadata.jar
    ├── odpswriter
    │   └── flinkx-odps-writer-feat_1.10_4.3.x_metadata.jar
    ├── opentsdbreader
    │   └── flinkx-opentsdb-reader-feat_1.10_4.3.x_metadata.jar
    ├── oracle9reader
    │   ├── flinkx-oracle9-reader-feat_1.10_4.3.x_metadata.jar
    │   └── flinkx-oracle9reader.zip
    ├── oracle9writer
    │   ├── flinkx-oracle9-writer-feat_1.10_4.3.x_metadata.jar
    │   └── flinkx-oracle9writer.zip
    ├── oraclelogminerreader
    │   └── flinkx-oraclelogminer-reader-feat_1.10_4.3.x_metadata.jar
    ├── oraclereader
    │   └── flinkx-oracle-reader-feat_1.10_4.3.x_metadata.jar
    ├── oraclewriter
    │   └── flinkx-oracle-writer-feat_1.10_4.3.x_metadata.jar
    ├── pgwalreader
    │   └── flinkx-pgwal-reader-feat_1.10_4.3.x_metadata.jar
    ├── phoenix5reader
    │   └── flinkx-phoenix5-reader-feat_1.10_4.3.x_metadata.jar
    ├── phoenix5writer
    │   └── flinkx-phoenix5-writer-feat_1.10_4.3.x_metadata.jar
    ├── polardbdreader
    │   └── flinkx-polardb-dreader-feat_1.10_4.3.x_metadata.jar
    ├── polardbreader
    │   └── flinkx-polardb-reader-feat_1.10_4.3.x_metadata.jar
    ├── polardbwriter
    │   └── flinkx-polardb-writer-feat_1.10_4.3.x_metadata.jar
    ├── postgresqlreader
    │   └── flinkx-postgresql-reader-feat_1.10_4.3.x_metadata.jar
    ├── postgresqlwriter
    │   └── flinkx-postgresql-writer-feat_1.10_4.3.x_metadata.jar
    ├── rediswriter
    │   └── flinkx-redis-writer-feat_1.10_4.3.x_metadata.jar
    ├── restapireader
    │   └── flinkx-restapi-reader-feat_1.10_4.3.x_metadata.jar
    ├── restapiwriter
    │   └── flinkx-restapi-writer-feat_1.10_4.3.x_metadata.jar
    ├── s3reader
    │   └── flinkx-s3-reader-feat_1.10_4.3.x_metadata.jar
    ├── s3writer
    │   └── flinkx-s3-writer-feat_1.10_4.3.x_metadata.jar
    ├── saphanareader
    │   └── flinkx-saphana-reader-feat_1.10_4.3.x_metadata.jar
    ├── saphanawriter
    │   └── flinkx-saphana-writer-feat_1.10_4.3.x_metadata.jar
    ├── socketreader
    │   └── flinkx-socket-reader-feat_1.10_4.3.x_metadata.jar
    ├── solrreader
    │   └── flinkx-solr-reader-feat_1.10_4.3.x_metadata.jar
    ├── solrwriter
    │   └── flinkx-solr-writer-feat_1.10_4.3.x_metadata.jar
    ├── sqlservercdcreader
    │   └── flinkx-sqlservercdc-reader-feat_1.10_4.3.x_metadata.jar
    ├── sqlserverreader
    │   └── flinkx-sqlserver-reader-feat_1.10_4.3.x_metadata.jar
    ├── sqlserverwriter
    │   └── flinkx-sqlserver-writer-feat_1.10_4.3.x_metadata.jar
    ├── streamreader
    │   └── flinkx-stream-reader-feat_1.10_4.3.x_metadata.jar
    ├── streamwriter
    │   └── flinkx-stream-writer-feat_1.10_4.3.x_metadata.jar
    └── websocketreader
        └── flinkx-websocket-reader-feat_1.10_4.3.x_metadata.jar
```
:::tip
配置好数据同步之后运行，如果一直提示等待运行，可以去monitor.log查看相应日志，确认flinkPluginRoot是否包含syncplugin的插件目录
:::

#### Spark

![Spark 配置](/img/readme/spark.png)

##### Spark 参数项

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

更多 Spark 参数项详见[官方文档](https://spark.apache.org/docs/2.1.3/configuration.html)

##### 自定义参数

| 参数项                 | 默认值                                                       | 说明                                       | 是否必填 |
| ---------------------- | ------------------------------------------------------------ | ------------------------------------------ | -------- |
| sparkPythonExtLibPath  | hdfs://ns1/dtInsight/spark240/pythons/pyspark.zip,hdfs://ns1/dtInsight/spark240/pythons/py4j-0.10.7-src.zip | pyspark.zip和py4j-0.10.7-src.zip路径       | 是       |
| sparkSqlProxyPath      | hdfs://ns1/dtInsight/spark240/client/spark-sql-proxy.jar     | spark-sql-proxy.jar路径，用于执行spark sql | 是       |
| sparkYarnArchive       | hdfs://ns1/dtInsight/spark240/jars                           | spark jars路径                             | 是       |
| yarnAccepterTaskNumber | 3                                                            | 允许的accepter任务数量                     | 否       |


**sparkSqlProxyPath**是Spark SQL任务运行的jar   
需要将pluginLibs/yarn2-hdfs2-spark210/spark-sql-proxy.jar 手动上传到对应的目录
**sparkYarnArchive**是Spark SQL程序运行时加载的包 直接将spark目录下的jar包上传到对应目录

:::tip
Flink、Spark可以添加自定义参数，在自定义参数中添加Flink、Spark官方参数来调整1任务提交参数信息
:::