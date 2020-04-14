#!/usr/bin/env bash

hadoopversion=$1
if [ ! -n "$hadoopversion" ]
then
    hadoopversion=3.0.0
fi
echo "Dependency ${hadoopversion} Building..."

mvn clean package -DskipTests -Dhadoop.version=${hadoopversion} -pl \
engine-plugins/dummy,\
engine-plugins/dtscript/dtscript-hadoop3/dtscript-client,\
engine-plugins/hadoop/hadoop3,\
engine-plugins/flink/flink180-hadoop3,\
engine-plugins/spark/spark-yarn-hadoop3,\
engine-plugins/kylin,\
engine-plugins/rdbs,\
engine-plugins/odps,\
engine-entrance \
-am -amd