#!/usr/bin/env bash
echo 'Dependency Hadoop-2.7.3&Kubernetes Building...'

mvn clean package -DskipTests -Dhadoop.version=2.7.3 -pl \
engine-worker/engine-plugins/dummy,\
engine-worker/engine-plugins/flink/common,\
engine-worker/engine-plugins/flink/k8s-hdfs2-flink110,\
engine-worker/engine-plugins/flink/k8s-nfs-flink110,\
engine-worker/engine-plugins/spark/k8s-hdfs2-spark240/spark-k8s-client,\
engine-worker/engine-plugins/spark/k8s-hdfs2-spark240/spark-sql-proxy,\
engine-worker/engine-plugins/stores/nfs,\
engine-worker/engine-plugins/stores/hdfs2,\
engine-worker/engine-plugins/schedules/kubernetes,\
engine-worker/engine-plugins/kylin,\
engine-worker/engine-plugins/odps,\
engine-worker/engine-plugins/rdbs/mysql,\
engine-worker/engine-plugins/rdbs/oracle,\
engine-worker/engine-plugins/rdbs/sqlserver,\
engine-worker/engine-plugins/rdbs/hive,\
engine-worker/engine-plugins/rdbs/hive2,\
engine-worker/engine-plugins/rdbs/postgresql,\
engine-worker/engine-plugins/rdbs/impala,\
engine-worker/engine-plugins/rdbs/tidb,\
engine-worker/engine-plugins/rdbs/greenplum,\
engine-worker/engine-plugins/rdbs/presto,\
engine-worker/engine-plugins/rdbs/inceptor,\
engine-entrance \
-am
