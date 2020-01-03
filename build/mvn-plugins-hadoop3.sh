#!/usr/bin/env bash
echo 'Dependency Hadoop-3.0.0 Building...'

mvn clean package -DskipTests -Dhadoop.version=3.0.0 -pl \
engine-entrance, \
engine-plugins/dtscript/dtscript-hadoop3/dtscript-client,\
engine-plugins/hadoop/hadoop3,\
engine-plugins/flink/flink180-hadoop3,\
engine-plugins/spark/spark-yarn-hadoop3,\
engine-plugins/kylin,\
engine-plugins/rdbs,\
engine-plugins/odps \
-am -amd