#!/usr/bin/env bash
echo 'Dependency Hadoop-2.7.3&Yarn Building...'

mvn clean package -DskipTests -Dhadoop.version=2.7.3 -pl \
engine-worker/engine-plugins/dummy,\
engine-worker/engine-plugins/flink/flink180-hadoop2,\
engine-worker/engine-plugins/flink/flink1100-hadoop2,\
engine-worker/engine-plugins/flink/flink180-HW,\
engine-worker/engine-plugins/spark/spark-yarn-hadoop2,\
engine-worker/engine-plugins/dtscript/dtscript-hadoop2/dtscript-client,\
engine-worker/engine-plugins/learning/learning-hadoop2/learning-client,\
engine-worker/engine-plugins/hadoop/hadoop2,\
engine-worker/engine-plugins/kylin,\
engine-worker/engine-plugins/rdbs,\
engine-worker/engine-plugins/odps,\
engine-entrance \
-am -amd