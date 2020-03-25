#!/usr/bin/env bash
echo 'Dependency Hadoop-2.7.3 Building...'

mvn clean package -Dhadoop.version=2.7.3 -pl \
plugins/flink/flink180-hadoop2,\
plugins/flink/flink180-HW,\
plugins/spark/spark-yarn-hadoop2,\
plugins/dtscript/dtscript-hadoop2/dtscript-client,\
plugins/learning/learning-hadoop2/learning-client,\
plugins/hadoop/hadoop2,\
plugins/kylin,\
plugins/rdbs,\
plugins/odps,\
entrance \
-am -amd