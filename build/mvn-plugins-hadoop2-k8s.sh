#!/usr/bin/env bash
echo 'Dependency Hadoop-2.7.3&Kubernetes Building...'

mvn clean package -DskipTests -Dhadoop.version=2.7.3 -pl \
engine-plugins/dummy,\
engine-plugins/flink/flink1100-kubernetes,\
engine-plugins/hadoop/hadoop2,\
engine-plugins/kylin,\
engine-plugins/rdbs,\
engine-plugins/odps,\
engine-entrance \
-am -amd