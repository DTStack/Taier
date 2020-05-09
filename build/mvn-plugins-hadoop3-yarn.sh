#!/usr/bin/env bash

hadoopversion=$1
if [ ! -n "$hadoopversion" ]
then
    hadoopversion=3.0.0
fi
echo "Dependency ${hadoopversion}&Yarn Building..."

mvn clean package -DskipTests -Dhadoop.version=${hadoopversion} -Dhivejdbc.version=2.1.0 -pl \
engine-worker/engine-plugins/dummy,\
engine-worker/engine-plugins/dtscript/dtscript-hadoop3/dtscript-client,\
engine-worker/engine-plugins/hadoop/hadoop3,\
engine-worker/engine-plugins/flink/yarn3-hdfs3-flink180,\
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark210,\
engine-worker/engine-plugins/kylin,\
engine-worker/engine-plugins/rdbs,\
engine-worker/engine-plugins/odps,\
engine-entrance \
-am -amd