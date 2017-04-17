-----------------------------
flink-dist_2.11-1.2.0_20170331.jar
修改flink和zk通信的bug,修改ZooKeeperUtils
添加	:
System.setProperty("zookeeper.sasl.client", String.valueOf(!disableSaslClient));
------------------------------
spark-core_2.11-2.1.2.jar
官网包存在问题:
    在调用已注册的hive udf时候.如果resource_uri是hdfs地址,无法通过metastore里面存储的
    hdfs加载对应的jar资源.导致调用已经注册udf的时候会提示找不到该udf.
解决:
    修改SparkContext.scala
    添加: URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
注意: git上已经有提交了修改的代码。等之后出现解决的版本后升级到对应的版本。
------------------------------