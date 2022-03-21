#!/usr/bin/env bash
echo 'taier Building...'

mvn clean package -DskipTests -T 1C -pl \
taier-common,\
taier-worker/taier-plugins/dummy,\
taier-worker/taier-plugins/flink/common,\
taier-worker/taier-plugins/flink/yarn3-hdfs3-flink112,\
taier-worker/taier-plugins/flink/yarn2-hdfs2-flink112,\
taier-worker/taier-plugins/spark/yarn3-hdfs3-spark210/spark-yarn-client,\
taier-worker/taier-plugins/spark/yarn3-hdfs3-spark210/spark-sql-proxy,\
taier-worker/taier-plugins/spark/yarn2-hdfs2-spark210/spark-yarn-client,\
taier-worker/taier-plugins/spark/yarn2-hdfs2-spark210/spark-sql-proxy,\
taier-worker/taier-plugins/hadoop/yarn3-hdfs3-hadoop3,\
taier-worker/taier-plugins/hadoop/yarn2-hdfs2-hadoop2,\
taier-worker/taier-plugins/stores/hdfs2,\
taier-worker/taier-plugins/stores/hdfs3,\
taier-worker/taier-plugins/schedules/yarn2,\
taier-worker/taier-plugins/schedules/yarn3,\
taier-worker/taier-plugins/rdbs/hive,\
taier-worker/taier-plugins/rdbs/hive2,\
taier-worker/taier-plugins/rdbs/hive3 \
-am