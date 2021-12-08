# flink150插件

## 1. 配置样例

```
{
    "again": 1,
    "appType": 1,
    "applicationPriority": 514762768,
    "attachJarInfos": [],
    "classArgs": "",
    "componentVersion": "1.10",
    "computeType": "BATCH",
    "confProperties": {},
    "coreJarInfo": {
        "jarPath": "/opt/dtstack/110_flinkplugin//syncplugin/flinkx-feat_1.10_4.3.x_metadata.jar"
    },
    "deployMode": 2,
    "engineType": "flink",
    "generateTime": 1632719879462,
    "groupName": "default_b",
    "isFailRetry": true,
    "jobCallBack": {},
    "jobName": "P_111_2021_09_27_14_52-111-20210927000000",
    "jobType": "SYNC",
    "lackingCount": 0,
    "maxRetryNum": 3,
    "pluginInfo": "{
        "typeName": "yarn2-hdfs2-hadoop2",
        "cluster": "default",
        "metrics.reporter.promgateway.port": "9091",
        "prometheusHost": "172.16.23.25",
        "yarnAccepterTaskNumber": "3",
        "env.java.opts": "-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8",
        "flinkJarPath": "/opt/dtstack/flink110_lib",
        "high-availability.cluster-id": "/default",
        "metrics.reporter.promgateway.jobName": "110job",
        "security.kerberos.login.contexts": "KafkaClient",
        "high-availability.zookeeper.path.root": "/flink110",
        "metrics.reporter.promgateway.class": "org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter",
        "high-availability.storageDir": "hdfs://ns1/flink110/ha",
        "sessionStartAuto": "true",
        "prometheusClass": "com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter",
        "gatewayJobName": "pushgateway",
        "pluginLoadMode": "shipfile",
        "deployMode": 2,
        "hadoopConf": {...},
        "state.backend.incremental": "true",
        "flinkSessionSlotCount": "10",
        "metrics.reporter.promgateway.deleteOnShutdown": "true",
        "high-availability.zookeeper.quorum": "172.16.22.210:2181",
        "yarn.application-attempt-failures-validity-interval": "3600000",
        "state.backend": "RocksDB",
        "metrics.reporter.promgateway.host": "172.16.23.25",
        "prometheusPort": "9090",
        "remotePluginRootDir": "/opt/dtstack/110_flinkplugin/",
        "state.checkpoints.num-retained": "11",
        "blob.service.cleanup.interval": "900",
        "hiveConf": {
            "maxJobPoolSize": "",
            "password": "",
            "minJobPoolSize": "",
            "jdbcUrl": "jdbc:hive2://172.16.101.161:10004/%s",
            "queue": "",
            "username": ""
        },
        "jarTmpDir": "./tmp110",
        "typeName": "yarn2-hdfs2-flink110",
        "monitorAcceptedApp": "false",
        "state.savepoints.dir": "hdfs://ns1/dtInsight/flink110/savepoints",
        "sftpConf": {...},
        "clusterMode": "session",
        "flinkPluginRoot": "/opt/dtstack/110_flinkplugin/",
        "metrics.reporter.promgateway.randomJobNameSuffix": "true",
        "yarn.application-attempts": "0",
        "taskmanager.numberOfTaskSlots": "1",
        "sessionRetryNum": "6",
        "remoteFlinkJarPath": "",
        "taskmanager.heap.mb": "1024",
        "jobmanager.archive.fs.dir": "hdfs://ns1/dtInsight/flink110/completed-jobs",
        "checkSubmitJobGraphInterval": "0",
        "classloader.resolve-order": "parent-first",
        "web.timeout": "100000",
        "jobmanager.heap.mb": "2048",
        "yarnConf": {...},
        "classloader.dtstack-cache": "true",
        "jobstore.expiration-time": "900",
        "high-availability": "ZOOKEEPER",
        "submitTimeout": "5",
        "md5zip": "27d0669d9d034e44eab2d6f3d235c2a9",
        "namespace": "b",
        "tenantId": 1,
        "queue": "b",
        "state.checkpoints.dir": "hdfs://ns1/dtInsight/flink110/checkpoints"
    }",
    "priority": 1632720879462,
    "priorityLevel": 10,
    "queueSourceType": 0,
    "sql": "",
    "submitCacheTime": 1632719879000,
    "submitExpiredTime": 0,
    "taskId": "2b6b49a9-13a9-45bc-b17e-bd8fe6bec91d",
    "tenantId": 1,
    "type": 1,
    "userId": 1
}
```

## 2. 参数说明
	
* **pluginInfo.'typeName'**

 	* 描述：任务类型是数据同步的任务填写对应的flink插件版本
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'cluster'**

 	* 描述：集群名称，根据控制台中租户所配置的集群
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'queue'**

 	* 描述：队列名称，根据控制台中租户所配置的队列
 		
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
