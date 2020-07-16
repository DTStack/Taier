#!/usr/bin/env bash
echo 'Dependency Hadoop-2.7.3&Kubernetes Building...'

mvn clean package -DskipTests -Dhadoop.version=2.7.3 -pl \
engine-worker/engine-plugins/flink/k8s-hdfs2-flink110,\
engine-entrance \
-am
