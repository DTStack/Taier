# flink150插件

## 1. 配置样例

```
{
    "isFailRetry": false,
    "sqlText": "",
    "computeType": 1,
    "exeArgs": "-jobid P_oracle2hive0114_2019_06_03_31_05-oracle2hive0114-20190603000000 -job %7B%22job%22%3A%7B%22content%22%3A%5B%7B%22reader%22%3A%7B%22parameter%22%3A%7B%22password%22%3A%22abc123%22%2C%22customSql%22%3A%22%22%2C%22column%22%3A%5B%7B%22name%22%3A%22id%22%2C%22type%22%3A%22NUMBER%22%7D%2C%7B%22name%22%3A%22time_col%22%2C%22type%22%3A%22TIMESTAMP%22%7D%2C%7B%22name%22%3A%22name%22%2C%22type%22%3A%22VARCHAR2%22%7D%2C%7B%22name%22%3A%22date_col%22%2C%22type%22%3A%22DATE%22%7D%5D%2C%22connection%22%3A%5B%7B%22password%22%3A%22abc123%22%2C%22jdbcUrl%22%3A%5B%22jdbc%3Aoracle%3Athin%3A%40172.16.8.178%3A1521%3Axe%22%5D%2C%22table%22%3A%5B%22order_test%22%5D%2C%22username%22%3A%22dtstack%22%7D%5D%2C%22splitPk%22%3A%22id%22%2C%22sourceIds%22%3A%5B147%5D%2C%22username%22%3A%22dtstack%22%7D%2C%22name%22%3A%22oraclereader%22%7D%2C%22writer%22%3A%7B%22parameter%22%3A%7B%22fileName%22%3A%22pt%3D201906%22%2C%22column%22%3A%5B%7B%22name%22%3A%22id%22%2C%22index%22%3A0%2C%22type%22%3A%22decimal%2810%2C0%29%22%7D%2C%7B%22name%22%3A%22time_col%22%2C%22index%22%3A1%2C%22type%22%3A%22timestamp%22%7D%2C%7B%22name%22%3A%22name%22%2C%22index%22%3A2%2C%22type%22%3A%22string%22%7D%2C%7B%22name%22%3A%22date_col%22%2C%22index%22%3A3%2C%22type%22%3A%22date%22%7D%5D%2C%22writeMode%22%3A%22overwrite%22%2C%22fieldDelimiter%22%3A%22%5Cu0001%22%2C%22encoding%22%3A%22utf-8%22%2C%22fullColumnName%22%3A%5B%22id%22%2C%22time_col%22%2C%22name%22%2C%22date_col%22%5D%2C%22path%22%3A%22hdfs%3A%2F%2Fns1%2Fuser%2Fhive%2Fwarehouse%2Fds_test.db%2Forder_test%22%2C%22password%22%3A%22abc123%22%2C%22partition%22%3A%22pt%3D201906%22%2C%22hadoopConfig%22%3A%7B%22dfs.ha.namenodes.ns1%22%3A%22nn1%2Cnn2%22%2C%22dfs.namenode.rpc-address.ns1.nn2%22%3A%22node002%3A9000%22%2C%22dfs.client.failover.proxy.provider.ns1%22%3A%22org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider%22%2C%22dfs.namenode.rpc-address.ns1.nn1%22%3A%22node001%3A9000%22%2C%22dfs.nameservices%22%3A%22ns1%22%7D%2C%22defaultFS%22%3A%22hdfs%3A%2F%2Fns1%22%2C%22connection%22%3A%5B%7B%22jdbcUrl%22%3A%22jdbc%3Ahive2%3A%2F%2Fnode001%3A10000%2Fds_test%22%2C%22table%22%3A%5B%22order_test%22%5D%7D%5D%2C%22fileType%22%3A%22orc%22%2C%22sourceIds%22%3A%5B144%5D%2C%22username%22%3A%22dtstack%22%2C%22fullColumnType%22%3A%5B%22decimal%2810%2C0%29%22%2C%22timestamp%22%2C%22string%22%2C%22date%22%5D%7D%2C%22name%22%3A%22hdfswriter%22%7D%7D%5D%2C%22setting%22%3A%7B%22restore%22%3A%7B%22maxRowNumForCheckpoint%22%3A0%2C%22isRestore%22%3Afalse%2C%22restoreColumnName%22%3A%22%22%2C%22restoreColumnIndex%22%3A0%7D%2C%22errorLimit%22%3A%7B%22record%22%3A100%2C%22percentage%22%3A0.0%7D%2C%22speed%22%3A%7B%22bytes%22%3A2097152%2C%22channel%22%3A1%7D%7D%7D%7D",
    "pluginInfo": {
        "cluster": "default",
        "queue": "c",
        "typeName": "flink150",
        "flinkZkAddress": "172.16.10.34:2181,172.16.10.45:2181,172.16.10.86:2181",
        "flinkHighAvailabilityStorageDir": "/flink154_xq/ha",
        "flinkZkNamespace": "/flink154_xq",
        "flinkClusterId": "/default",
        "remotePluginRootDir": "/opt/dtstack/150_flinkplugin",
        "flinkPluginRoot": "/opt/dtstack/150_flinkplugin",
        "flinkJarPath": "/opt/dtstack/flink-1.5.4/lib",
        "clusterMode": "yarn",
        "flinkYarnMode": "PER_JOB",
        "jarTmpDir": "../tmp150",
        "test.params": "default",
        "jobmanagerArchiveFsDir": "hdfs://ns1/flink154_1/completed-jobs",
        "flinkJobHistory": "http://node003:8082",
        "state.backend": "filesystem",
        "state.checkpoints.dir": "hdfs://ns1/flink155_1/checkpoints",
        "state.savepoints.dir": "hdfs://ns1/flink155_1/savepoints",
        "state.checkpoints.num-retained": "50",
        "reporterClass": "org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter",
        "gatewayHost": "172.16.10.45",
        "gatewayPort": "9091",
        "gatewayJobName": "xcJob",
        "deleteOnShutdown": "FALSE",
        "randomJobNameSuffix": "TRUE",
        "hiveConf": {
            "jdbcIdel": "1",
            "queryTimeout": "60000",
            "password": "",
            "maxRows": "5000",
            "minPoolSize": "5",
            "useConnectionPool": "true",
            "jdbcUrl": "jdbc:hive2://node001:10000/%s",
            "driverClassName": "org.apache.hive.jdbc.HiveDriver",
            "checkTimeout": "60000",
            "maxPoolSize": "20",
            "initialPoolSize": "5",
            "username": ""
        },
        "hadoopConf": {
            "dfs.ha.namenodes.ns1": "nn1,nn2",
            "fs.defaultFS": "hdfs://ns1",
            "dfs.client.failover.proxy.provider.ns1": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider",
            "dfs.namenode.rpc-address.ns1.nn2": "node002:9000",
            "dfs.namenode.rpc-address.ns1.nn1": "node001:9000",
            "dfs.nameservices": "ns1",
            "fs.hdfs.impl.disable.cache": "true",
            "fs.hdfs.impl": "org.apache.hadoop.hdfs.DistributedFileSystem"
        },
        "yarnConf": {
            "yarn.resourcemanager.address.rm1": "node001:8032",
            "yarn.resourcemanager.webapp.address.rm2": "node002:8088",
            "yarn.resourcemanager.webapp.address.rm1": "node001:8088",
            "yarn.resourcemanager.ha.rm-ids": "rm1,rm2",
            "yarn.resourcemanager.address.rm2": "node002:8032",
            "yarn.resourcemanager.ha.enabled": "true",
            "yarn.nodemanager.remote-app-log-dir": "/tmp/logs"
        },
        "tenantId": 17
    },
    "engineType": "flink",
    "taskParams": "sql.checkpoint.cleanup.mode = false\nmr.job.parallelism = 1\nspark.sql.parquet.enableVectorizedReader = false\nflinkTaskRunMode = new\nsql.checkpoint.interval = 600000\nsql.env.parallelism = 1\n",
    "maxRetryNum": 0,
    "taskType": 2,
    "groupName": "default_c",
    "name": "P_oracle2hive0114_2019_06_03_31_05-oracle2hive0114-20190603000000",
    "tenantId": 1,
    "taskId": "9a3abbbd"
}
```

## 2. 参数说明
	
* **pluginInfo.'typeName'**

 	* 描述：任务类型是数据同步的任务填写对应的flink插件版本，此处为 `flink150`
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'cluster'**

 	* 描述：集群名称，根据控制台（Console）中租户所配置的集群
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'queue'**

 	* 描述：队列名称，根据控制台（Console）中租户所配置的队列
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'flinkZkAddress'**

 	* 描述：运行 flink 任务时所配置的 Zookeeper 地址，可以参数 `flink-conf.yaml` 进行配置
 		
	* 必选：是 <br />

	* 默认值：无 <br />	
	
* **pluginInfo.'flinkHighAvailabilityStorageDir'**

 	* 描述：运行 flink 任务时job支持ha所存储的路径，可以参数 `flink-conf.yaml` 进行配置
 		
	* 必选：是 <br />

	* 默认值：无 <br />	

* **pluginInfo.'flinkZkNamespace'**

 	* 描述：Zookeeper的存储路径，可以参数 `flink-conf.yaml` 进行配置
 		
	* 必选：是 <br />

	* 默认值：无 <br />	

* **pluginInfo.'flinkClusterId'**

 	* 描述：flink 任务以 yarnSession 模式运行时的 clusterId，可以参数 `flink-conf.yaml` 进行配置
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'remotePluginRootDir'**

 	* 描述：任务运行在计算节点上时 flink 插件的根目录
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'flinkPluginRoot'**

 	* 描述：engine所运行节点上的 flink 插件的根目录
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'flinkJarPath'**

 	* 描述：engine所运行节点上的 flink 的安装目录下的 lib 目录地址，以perjob模式运行时需要读取该路径下的jar文件
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'clusterMode'**

 	* 描述：flink 任务的运行模式 yarn/standalone
 		
	* 必选：是 <br />

	* 默认值：yarn

* **pluginInfo.'flinkYarnMode'**

 	* 描述：流计算任务的运行模式，NEW,LEGACY,PER_JOB
 		
	* 必选：是 <br />

	* 默认值：LEGACY

* **pluginInfo.'jobmanagerArchiveFsDir'**

 	* 描述：已完成job的归档路径，可以参考 `flink-conf.yaml` 进行配置
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'flinkJobHistory'**

 	* 描述：HistoryServer允许查询已完成job的状态和统计信息，需要预先启动运行HistoryServer，根据启动的节点进行配置
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'state.backend'**

 	* 描述：当job开启checkpoint时，flink的job会将计算的状态信息存储在checkpoint中以防节点恢复时数据丢失，这些都取决于 `State Backend` 的选择
 		
	* 必选：否 <br />

	* 默认值：MemoryStateBackend

* **pluginInfo.'state.checkpoints.dir'**

 	* 描述：checkpoint的存储路径
 		
	* 必选：否 <br />

	* 默认值：MemoryStateBackend

* **pluginInfo.'state.savepoints.dir'**

 	* 描述：savepoint的存储路径
 		
	* 必选：否 <br />

	* 默认值：无 <br />

* **pluginInfo.'state.checkpoints.num-retained'**

 	* 描述：checkpoint保留的数量
 		
	* 必选：否 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'reporterClass'**

 	* 描述：flink 任务将统计信息metric存到promethues上的配置，可以参考 `flink-conf.yaml` 进行配置
 		
	* 必选：否 <br />

	* 默认值：无 <br />	
	
* **pluginInfo.'gatewayHost'**

 	* 描述：flink 任务将统计信息metric存到promethues上的配置，可以参考 `flink-conf.yaml` 进行配置
 		
	* 必选：否 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'gatewayPort'**

 	* 描述：flink 任务将统计信息metric存到promethues上的配置，可以参考 `flink-conf.yaml` 进行配置
 		
	* 必选：否 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'gatewayJobName'**

 	* 描述：flink 任务将统计信息metric存到promethues上的配置，可以参考 `flink-conf.yaml` 进行配置
 		
	* 必选：否 <br />

	* 默认值：无 <br />

* **pluginInfo.'deleteOnShutdown'**

 	* 描述：flink 任务将统计信息metric存到promethues上的配置，可以参考 `flink-conf.yaml` 进行配置
 		
	* 必选：否 <br />

	* 默认值：无 <br />

* **pluginInfo.'randomJobNameSuffix'**

 	* 描述：flink 任务将统计信息metric存到promethues上的配置，可以参考 `flink-conf.yaml` 进行配置
 		
	* 必选：否 <br />

	* 默认值：无 <br />
