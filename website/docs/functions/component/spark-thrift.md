---
title: Spark-Thrift
sidebar_label: Spark-Thrift
---
## 启动Spark-Thrift

1. 下载`spark`安装包，我们选择的是[spark2.1.3b](https://archive.apache.org/dist/spark/spark-2.1.3/spark-2.1.3-bin-hadoop2.7.tgz)
2. 解压spark-2.1.3-bin-hadoop2.7.tgz
3. 将core-site.xml、hdfs-site.xml、yarn-site.xml、hive-site.xml拷贝到${SPARK_HOME}/conf目录下
4. 启动spark thriftserver:
```shell
$   cd ${SPARK_HOME}/sbin && sh start-thriftserver.sh
```

## 配置Spark-Thrift
已配置前置组件
- [x] SFTP
- [x] YARN
- [x] HDFS
- [x] Spark

选择好对应的版本 填写相关参数信息即可
:::caution
jdbc配置中需要带上%s 如: jdbc:hive2://172.16.85.248:10000/%s
:::