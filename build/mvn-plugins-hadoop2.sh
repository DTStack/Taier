#!/usr/bin/env bash
echo 'Dependency Hadoop-2.7.3&Yarn Building...'

hadoopversion=$1
if [ ! -n "$hadoopversion" ]
then
    hadoopversion=2.7.3
fi
echo "Dependency ${hadoopversion} Building..."

mvn clean package -DskipTests -Dhadoop.version=${hadoopversion} -Dhivejdbc.version=1.1.1 -pl \
engine-worker/engine-plugins/dummy,\
engine-worker/engine-plugins/flink/yarn2-hdfs2-flink180,\
engine-worker/engine-plugins/flink/yarnHW-hdfsHW-flink180HW,\
engine-worker/engine-plugins/flink/k8s-hdfs2-flink110,\
engine-worker/engine-plugins/flink/yarn2-hdfs2-flink180,\
engine-worker/engine-plugins/spark/yarn2-hdfs2-spark210/spark-yarn-client,\
engine-worker/engine-plugins/spark/yarn2-hdfs2-spark210/spark-sql-proxy,\
engine-worker/engine-plugins/dtscript/yarn2-hdfs2-dtscript/dtscript-client,\
engine-worker/engine-plugins/learning/yarn2-hdfs2-learning/learning-client,\
engine-worker/engine-plugins/hadoop/yarn2-hdfs2-hadoop2,\
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
engine-entrance \
-am

