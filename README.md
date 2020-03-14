# Engine

## 1. 什么是Engine

> Engine！！！
><br/>~~~ 是数栈平台的核心应用，核心功能是将不同类型的任务（Job）提交到对应类型的执行引擎组件之上运行。


* ***向上*** 负责接收数栈各个应用application所提交的任务（Job）
* ***内部*** 负责任务的负载均衡 & 优先级调度
* ***向下*** 负责将各种类型的任务（Job）真正地提交（submit）到具体的执行引擎组件上

<div align=center>
	<img src=docs/images/dtinsight_artifact.png width=600 />
</div>

## 2. Engine 架构

Engine 在实现上仅依赖于mysql数据库与zookeeper分布式一致性服务，支持分布式部署。

<div align=center>
	<img src=docs/images/engine_artifact.png width=600 />
</div>

## 3. 快速起步

### 3.1 engine 运行环境

* JDK8及以上
* Mysql
* Zookeeper

### 3.2 执行引擎组件版本

* hadoop-2.7.3
* flink-1.5.4
* spark-2.1.0
* hive-2.1.1



### 3.3 打包

进入项目根目录，使用maven打包：

```
mvn clean package -Dmaven.test.skip
```

打包结束后，项目根目录下会产生plugin目录，plugin目录下存放已经编译好的各个类型的执行引擎插件的jar包

### 3.4 启动

```
bin/bash.sh
```


## 4. 任务（Job）模版

从最高空俯视，一个engine任务（Job）的主要构成很简单，如下：

```
{
    "name": "...",
    "taskId": "...",
    "computeType": "...",
    "engineType": "...",
    "taskType": "...",
    "sqlText": "...",
    "exeArgs": "...",
    "taskParams": "...",
    "maxRetryNum": "...",
    "groupName": "...",
    "pluginInfo": {...}
}
```

### 4.1 名词解释
1. name: 任务名称

2. taskId: 全局唯一标识，定位任务（Job）时的主要条件之一

3. computeType: 计算类型
	* stream（流计算）
	* batch（离线计算）
	
4. engineType: 执行引擎组件类型
    * flink
    * spark
    * learning
    * dtyarnshell
    * mysql
    * oracle
    * sqlserver
    * maxcompute
    * hadoop
    * hive
    * postgreSQL
   	
5. taskType: 任务类型
	* 0：sql任务
	* 1：mr任务
	* 2：sync数据同步任务
	* 3：python任务
	
6. sqlText: sql文本
7. exeArgs: 执行参数
8. taskParams: 环境参数
9. maxRetryNum: 最大重试次数
10. groupName: 队列名称
11. pluginInfo: 插件信息

### 4.2 pluginInfo

pluginInfo内容包括执行引擎插件的基础配置信息和hiveConf、hadoopConf和yarnConf三部分，分别描述hive、hadoop和yarn的配置信息

```
"pluginInfo":{
    "...":"...",//此处省略具体某一种执行引擎插件的基础配置信息，详情见控制台（Console）多集群管理
    "hiveConf": {...},
    "hadoopConf": {...},
    "yarnConf": {...},
}
```

### 4.3 hiveConf

hiveConf内容包括hive数据源的 jdbcUrl、username和password，用户名或密码为空填写空字符串

```
    "hiveConf":{
        "password": "...",
        "jdbcUrl": "...",
        "username": "..."
    }

```

### 4.4 hadoopConf

hadoopConf内容包括hadoop的链接信息，支持HA模式与非HA模式


#### 4.4.1 HA模式

以 nameservices=ns1、namenode=nn1,nn2 为例

```HA模式

    "hadoopConf":{
        "fs.defaultFS": "hdfs://ns1",
        "dfs.ha.namenodes.ns1": "nn1,nn2",
        "dfs.client.failover.proxy.provider.ns1": "...",
        "dfs.namenode.rpc-address.ns1.nn2": "...",
        "dfs.namenode.rpc-address.ns1.nn1": "...",
        "dfs.nameservices": "ns1",
        "fs.hdfs.impl.disable.cache": "true",
        "fs.hdfs.impl": "..."
    }

```

#### 4.4.2 非HA模式
```非HA模式

    "hadoopConf":{
        "fs.defaultFS": "...",
        "fs.hdfs.impl.disable.cache": "true",
        "fs.hdfs.impl": "..."
    }

```

### 4.5 yarnConf

yarnConf内容包括yarn的链接信息，支持HA模式与非HA模式


#### 4.5.1 HA模式

以 rm-ids=rm1,rm2 为例

```HA模式

    "yarnConf":{
        "yarn.resourcemanager.ha.enabled": "true",
        "yarn.resourcemanager.ha.rm-ids": "rm1,rm2",
        "yarn.resourcemanager.address.rm1": "...",
        "yarn.resourcemanager.address.rm2": "...",
        "yarn.resourcemanager.webapp.address.rm2": "...",
        "yarn.resourcemanager.webapp.address.rm1": "...",
        "yarn.nodemanager.remote-app-log-dir": "..."
    }

```

#### 4.5.2 非HA模式
```非HA模式

    "yarnConf":{
        "yarn.resourcemanager.ha.enabled": "false",
        "yarn.resourcemanager.address": "...",
        "yarn.resourcemanager.webapp.address": "...",
        "yarn.nodemanager.remote-app-log-dir": "..."
    }

```

## 5. 执行引擎组件的插件

目前使用的频繁度，由上到下排序

* [flink150插件](docs/flink150.md)
* [spark_yarn插件](docs/spark_yarn.md)
* [dtyarnshell插件](docs/dtyarnshell.md)
* [mysql插件](docs/mysql.md)
* [oracle插件](docs/oracle.md)
* [sqlserver插件](docs/sqlserver.md)
* [maxcompute插件](docs/maxcompute.md)
* [hive插件](docs/hive.md)
* [postgresql插件](docs/postgresql.md)
* [learning插件](docs/learning.md)
* [hadoop插件](docs/hadoop.md)

## 6. 其他参考资料

* [在CDH集群中启用及配置Kerberos](docs/在CDH集群中启用及配置Kerberos.docx) @言蹊
* [开源集群配置Kerberos认证](docs/开源集群配置Kerberos认证.docx) @言蹊
* [engine引擎](docs/engine.key) @偷天