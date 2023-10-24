---
title: Spark
sidebar_label: Spark
---


## 配置Spark
已配置前置组件
- [x] SFTP
- [x] YARN
- [x] HDFS

![Spark 配置](/img/readme/spark.png)

### 参数说明

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


:::tip
Spark在自定义参数中添加Spark官方参数来调整任务提交参数信息
> 更多 Spark 参数项详见[官方文档](https://spark.apache.org/docs/2.1.3/configuration.html)
:::

### 自定义参数

| 参数项                 | 默认值                                                       | 说明                                       | 是否必填 |
| ---------------------- | ------------------------------------------------------------ | ------------------------------------------ | -------- |
| sparkPythonExtLibPath  | hdfs://ns1/dtInsight/spark210/pythons/pyspark.zip<br/>hdfs://ns1/dtInsight/spark210/pythons/py4j-0.10.7-src.zip | pyspark.zip和py4j-0.10.7-src.zip路径       | 是       |
| sparkSqlProxyPath      | hdfs://ns1/dtInsight/spark210/client/spark-sql-proxy.jar     | spark-sql-proxy.jar路径，用于执行spark sql | 是       |
| sparkYarnArchive       | hdfs://ns1/dtInsight/spark210/jars                           | spark jars路径                             | 是       |
| yarnAccepterTaskNumber | 3                                                            | 允许的accepter任务数量                     | 否       |

:::caution
**sparkSqlProxyPath**是Spark SQL任务运行的jar,需要将pluginLibs/yarn2-hdfs2-spark210/spark-sql-proxy.jar 手动上传到HDFS对应的目录  
**sparkYarnArchive**是Spark SQL程序运行时加载的外部包,需要将spark目录下的jar包上传到对应HDFS目录
> 我们选择的是[spark2.1.3](https://archive.apache.org/dist/spark/spark-2.1.3/spark-2.1.3-bin-hadoop2.7.tgz) 
[spark3.2.0](https://archive.apache.org/dist/spark/spark-3.2.0/spark-3.2.0-bin-hadoop2.7.tgz)  
  TDH、CDH等Hadoop集群 需要根据具体环境实际调整
:::

