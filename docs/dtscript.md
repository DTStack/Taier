# dtyarnshell插件

## 1. 配置样例

```
{
    "isFailRetry": false,
    "sqlText": "",
    "computeType": 1,
    "exeArgs": "--files hdfs://ns1/rdos/batch/python_141_99_gfdvvv_1561900891520.py --python-version 2 --app-type python2 --app-name gfdvvv",
    "pluginInfo": {
        "typeName": "dtyarnshell",
        "cluster": "default",
        "queue": "c",
        "jlogstashRoot": "/opt/dtstack/jlogstash",
        "javaHome": "/opt/dtstack/java/bin",
        "hadoopHomeDir": "/opt/dtstack/hadoop",
        "python3Path": "/opt/dtstack/miniconda3/bin/python3",
        "python2Path": "/opt/dtstack/miniconda2/bin/python2",
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
    "engineType": "dtyarnshell",
    "taskParams": "worker.memory=512m worker.cores=1",
    "maxRetryNum": 0,
    "taskType": 3,
    "jobResource": "default_c",
    "sourceType": 2,
    "name": "run_python_task_1561900891604",
    "tenantId": 1,
    "taskId": "f4fe53bb" 
}
```

## 2. 参数说明

* **pluginInfo.'typeName'**

 	* 描述：任务类型是 dtyarnshell 的任务填写 `dtyarnshell`
 		
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

* **pluginInfo.'jlogstashRoot'**

 	* 描述：jlogstash 插件的根目录，用于流计算的采集任务
 		
	* 必选：是 <br />

	* 默认值：无 <br />	
	
* **pluginInfo.'javaHome'**

 	* 描述：java的执行环境，填写 JAVA_HOME/bin 的路径
 		
	* 必选：是 <br />

	* 默认值：无 <br />	
	
* **pluginInfo.'hadoopHomeDir'**

 	* 描述：与 hadoop 相关的配置，填写 $HADOOP_HOME 的路径
 		
	* 必选：是 <br />

	* 默认值：无 <br />	
	
* **pluginInfo.'python3Path'**

 	* 描述：python 3.X 版本的bin目录下的python3执行命令，用于python2任务
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'python2Path'**

 	* 描述：python 2.X 版本的bin目录下的python2执行命令，用于python3任务
 		
	* 必选：是 <br />

	* 默认值：无 <br />	