#!/usr/bin/env bash

hadoopversion=$1
if [ ! -n "$hadoopversion" ]
then
    hadoopversion=3.0.0
fi
echo "Dependency ${hadoopversion} Building..."

mvn clean package -DskipTests -Dhadoop.version=${hadoopversion} -pl \
plugins/dtscript/dtscript-hadoop3/dtscript-client,\
plugins/hadoop/hadoop3,\
plugins/flink/flink180-hadoop3,\
plugins/spark/spark-yarn-hadoop3,\
plugins/kylin,\
plugins/rdbs,\
plugins/odps,\
entrance \
-am -amd