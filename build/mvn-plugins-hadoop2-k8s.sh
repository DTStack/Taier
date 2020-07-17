#!/usr/bin/env bash
echo 'Dependency Hadoop-2.7.3&Kubernetes Building...'

mvn clean package -DskipTests -Dhadoop.version=2.7.3 -pl \
engine-worker/engine-plugins/dummy,\
engine-worker/engine-plugins/flink/k8s-hdfs2-flink110,\
engine-worker/engine-plugins/hadoop/k8s-hdfs2-hadoop2,\
engine-worker/engine-plugins/kylin,\
engine-worker/engine-plugins/odps,\
engine-worker/engine-plugins/rdbs/mysql,\
engine-worker/engine-plugins/rdbs/oracle,\
engine-worker/engine-plugins/rdbs/sqlserver,\
engine-worker/engine-plugins/rdbs/hive,\
engine-worker/engine-plugins/rdbs/postgresql,\
engine-worker/engine-plugins/rdbs/impala,\
engine-worker/engine-plugins/rdbs/tidb,\
engine-worker/engine-plugins/rdbs/greenplum,\
engine-entrance \
-am