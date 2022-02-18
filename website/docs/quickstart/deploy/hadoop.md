---
title: 集群依赖
sidebar_label: 集群依赖
---

# 集群部署
## spark thrift
1. 下载`spark`安装包，我们选择的是spark2.1.3b版本，下载地址：https://archive.apache.org/dist/spark/spark-2.1.3/spark-2.1.3-bin-hadoop2.7.tgz
2. 解压spark-2.1.3-bin-hadoop2.7.tgz
3. 将core-site.xml、hdfs-site.xml、yarn-site.xml、hive-site.xml拷贝到${SPARK_HOME}/conf目录下
4. 启动spark thriftserver:
```shell
$   cd ${SPARK_HOME}/sbin && sh start-thriftserver.sh
```

## flinkx
依赖`flinkx` [1.10](https://github.com/DTStack/flinkx/releases/tag/1.10.5) 版本
[flink源码编译](https://github.com/DTStack/flinkx/blob/master/docs/quickstart.md)