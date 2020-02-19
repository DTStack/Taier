# spark_yarn插件

## 1. 配置样例

```
{
 "isFailRetry": false,
 "sqlText": "use ds_test; create table select_sql_temp_table_1155693631148712 stored as orc as select * from (select count(1) from 01s_h2)temp",
 "computeType": 1,
 "pluginInfo": {
    "typeName": "spark_yarn",
     "cluster": "default",
     "queue": "c",
     "confHdfsPath": "/hadoop_config/default",
     "sparkYarnArchive": "/sparkjars/jars",
     "sparkSqlProxyPath": "/user/spark/spark-sql-proxy.jar",
     "sparkPythonExtLibPath": "/pythons/pyspark.zip,hdfs://ns1/pythons/py4j-0.10.4-src.zip",
     "spark.yarn.appMasterEnv.PYSPARK_PYTHON": "/opt/dtstack/miniconda3/bin/python",
     "spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON": "/opt/dtstack/miniconda3/bin/python",
     "carbonStorePath": "hdfs://ns1/user/hive/warehouse2/carbon.store",
     "spark.local.dir": "/tmp",
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
 "engineType": "spark",
 "taskParams": "##Driver程序使用的CPU核数,默认为1\r\n##driver.cores=1\r\n\r\n##Driver程序使用内存大小,默认512m\r\n##driver.memory=512m\r\n\r\n##对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\r\n##若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\r\n##driver.maxResultSize=1g\r\n\r\n##SparkContext 启动时是否记录有效 SparkConf信息,默认false\r\n##logConf=false\r\n\r\n\r\n##启动的executor的数量，默认为1\r\nexecutor.instances=1\r\n\r\n#每个executor使用的CPU核数，默认为1\r\nexecutor.cores=1\r\n\r\n##每个executor内存大小,默认512\r\n##executor.memory=512m\n\nspark.sql.parquet.enableVectorizedReader=false",
 "maxRetryNum": 0,
 "taskType": 0,
 "groupName": "default_c",
 "sourceType": 2,
 "name": "run_sql_task_1561907443389",
 "tenantId": 1,
 "taskId": "51467cc9"
}
```

## 2. 参数说明

* **pluginInfo.'typeName'**

 	* 描述：spark 任务以 yarn 方式提交并运行，此处为 `spark_yarn`
 		
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
	
* **pluginInfo.'sparkSqlProxyPath'**

 	* 描述：spark-sql-proxy.jar的路径，以运行 am
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'confHdfsPath'**

 	* 描述：spark 任务运行时所需要的 hadoop 配置文件在 hdfs 上的路径
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'sparkYarnArchive'**

 	* 描述：spark 任务运行时所需要的 jars 在 hdfs 上的路径
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'sparkSqlProxyPath'**

 	* 描述：spark-sql-proxy.jar的路径，用于向 yarn 提交application时作为 am
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'sparkPythonExtLibPath'**

 	* 描述：spark 机器学习任务所需要的额外jar包
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'spark.yarn.appMasterEnv.PYSPARK_PYTHON'**

 	* 描述：pyspark的相关配置
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON'**

 	* 描述：pyspark的相关配置
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'carbonStorePath'**

 	* 描述：carbon data 任务的数据存储路径
 		
	* 必选：是 <br />

	* 默认值：无 <br />