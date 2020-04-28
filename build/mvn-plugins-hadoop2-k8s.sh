#!/usr/bin/env bash
echo 'Dependency Hadoop-2.7.3&Kubernetes Building...'

mvn clean package -DskipTests -Dhadoop.version=2.7.3 -pl \
engine-worker/engine-plugins/dummy,\
engine-worker/engine-plugins/flink/flink1100-kubernetes,\
engine-worker/engine-plugins/hadoop/hadoop2,\
engine-worker/engine-plugins/kylin,\
engine-worker/engine-plugins/rdbs,\
engine-worker/engine-plugins/odps,\
engine-entrance \
-am -amd