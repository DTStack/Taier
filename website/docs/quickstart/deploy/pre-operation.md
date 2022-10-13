---
title: 部署依赖
sidebar_label: 部署依赖
---

# 部署依赖

## Datasoucex依赖

Taier需要依赖[DatasourceX](https://github.com/DTStack/DatasourceX/releases/tag) 去获取数据源表、字段信息  
下载DatasourceX源码 解压之后在配置文件中配置`datasource.plugin.path`即可   
Datasoucex解压后目录结构为

```shell
/data/datasourcex
├── aws_s3
├── clickhouse
├── db2
├── dmdb
├── doris
├── emq
├── es
├── es7
├── ftp
├── gbase
├── greenplum6
├── hbase
├── hbase2
├── hbase_gateway
├── hdfs
├── hive
├── hive1
├── hive3
├── impala
├── inceptor
├── influxdb
├── kafka
├── kingbase8
├── kudu
├── kylin
├── kylinrestful
├── libra
├── maxcompute
├── mongo
├── mysql5
├── mysql8
├── oceanbase
├── opentsdb
├── oracle
├── phoenix
├── phoenix4_8
├── phoenix5
├── postgresql
├── presto
├── redis
├── restful
├── s3
├── socket
├── solr
├── spark
├── sqlServer
├── sqlServer2017
├── vertica
└── websocket
```

