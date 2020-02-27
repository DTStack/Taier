#!/usr/bin/env bash
echo 'Dependency Hadoop-2.7.3 Building...'

mvn clean package -DskipTests -Dhadoop.version=2.7.3 -pl \
engine-plugins/flink/flink180-hadoop2,\
engine-plugins/flink/flink180-HW,\
engine-plugins/spark/spark-yarn-hadoop2,\
engine-plugins/dtscript/dtscript-hadoop2/dtscript-client,\
engine-plugins/learning/learning-hadoop2/learning-client,\
engine-plugins/hadoop/hadoop2,\
engine-plugins/kylin,\
engine-plugins/odps,\
engine-entrance \
-am -amd