#!/usr/bin/env bash
echo 'Dependency Hadoop-3.0.0 Building...'

mvn clean package -DskipTests -Dhadoop.version=3.0.0-cdh6.3.1 -pl \
plugins/dtscript/dtscript-hadoop3/dtscript-client,\
plugins/hadoop/hadoop3,\
plugins/flink/flink180-hadoop3,\
plugins/spark/spark-yarn-hadoop3,\
plugins/kylin,\
plugins/rdbs,\
plugins/odps,\
entrance \
-am -amd