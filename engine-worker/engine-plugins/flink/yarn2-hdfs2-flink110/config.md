
"pluginInfo": {

    //prometheus相关
    "metrics.reporter.promgateway.class": "org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter",
    "metrics.reporter.promgateway.host": "172.16.10.45",
    "metrics.reporter.promgateway.port": "9091",
    "metrics.reporter.promgateway.jobName":"mqTest01",
    "metrics.reporter.promgateway.randomJobNameSuffix":"TRUE",
    "metrics.reporter.promgateway.deleteOnShutdown":"TRUE",

    //"flinkJobHistory": "http://kudu2:8082",
    "historyserver.web.address":"kudu2",
    "historyserver.web.port":"8082",

    "high-availability":"ZOOKEEPER",
    //flinkClusterId
    "high-availability.cluster-id": "/default",
    //flinkZkNamespace
    "high-availability.zookeeper.path.root": "/flink1100",
    //flinkHighAvailabilityStorageDir，需要增加hdfs前缀
    "high-availability.storageDir": "hdfs://ns1/flink1100/ha",
    //flinkZkAddress
    "high-availability.zookeeper.quorum": "kudu1:2181,kudu2:2181,kudu3:2181",

    //jobmanagerArchiveFsDir，需要增加hdfs前缀
    "jobmanager.archive.fs.dir":"hdfs://ns1/flink1100/completed-job",


    "yarn.taskmanager.help.mb": 1024,
    "yarn.taskmanager.numberOfTaskSlots": 2,
    "yarn.taskmanager.numberOfTaskManager": 2,
    //需要增加hdfs前缀
    "state.checkpoints.dir": "hdfs://ns1/flink1100/checkpoints/metadata",
    "state.checkpoints.num-retained": "aaa",
    
    //不变的属于engine自定义参数
    "flinkJarPath": "/opt/dtstack/flink-1.8.1/lib/",
    "flinkSessionSlotCount": "2",
    "typeName": "flink1100-hadoop2",
    "jarTmpDir": "../tmp1100",
    "clusterMode": "yarn",
    "flinkPluginRoot": "/opt/dtstack/1100_flinkplugin",
    "remotePluginRootDir": "/opt/dtstack/1100_flinkplugin",
    "queue": "c",
    "cluster": "flink1100",
    "md5zip": "0e8bccfc6597ad9b5185e028938dff7d",
    "tenantId": 13,
    "yarn.jobmanager.help.mb": 1024,
    "openKerberos": false,
    
    
    "hiveConf": "...",
    "hadoopConf": "...",
    "yarnConf": "..."
}