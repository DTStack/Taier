-----------------------------
flink-dist_2.11-1.2.0_20170331.jar
修改flink和zk通信的bug,修改ZooKeeperUtils
添加	:
System.setProperty("zookeeper.sasl.client", String.valueOf(!disableSaslClient));
------------------------------