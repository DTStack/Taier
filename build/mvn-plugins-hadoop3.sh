#!/usr/bin/env bash

hadoopversion=$1
if [ ! -n "$hadoopversion" ]
then
    hadoopversion=3.0.0
fi
echo "Dependency ${hadoopversion} Building..."

mvn clean package -DskipTests -Dhadoop.version=${hadoopversion} -Dhivejdbc.version=2.1.0 -pl \
engine-worker/engine-plugins/dummy,\
engine-worker/engine-plugins/hadoop/yarn3-hdfs3-hadoop3,\
engine-worker/engine-plugins/dtscript/yarn3-hdfs3-dtscript/dtscript-client,\
engine-worker/engine-plugins/flink/yarn3-hdfs3-flink180,\
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark210/spark-yarn-client,\
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark210/spark-sql-proxy,\
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark240/spark-yarn-client,\
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark240/spark-sql-proxy,\
engine-worker/engine-plugins/kylin,\
engine-worker/engine-plugins/odps,\
engine-worker/engine-plugins/rdbs/mysql,\
engine-worker/engine-plugins/rdbs/oracle,\
engine-worker/engine-plugins/rdbs/sqlserver,\
engine-worker/engine-plugins/rdbs/hive,\
engine-worker/engine-plugins/rdbs/postgresql,\
engine-worker/engine-plugins/rdbs/impala,\
engine-worker/engine-plugins/rdbs/tidb,\
engine-entrance \
-am