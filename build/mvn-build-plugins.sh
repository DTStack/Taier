#!/usr/bin/env bash
echo 'DAGSchedulex Building...'

mvn clean package -DskipTests  -pl \
engine-common,\
engine-worker/engine-plugins/dummy,\
engine-worker/engine-plugins/flink/common,\
engine-worker/engine-plugins/flink/yarn3-hdfs3-flink110,\
engine-worker/engine-plugins/flink/yarn2-hdfs2-flink110,\
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark240/spark-yarn-client,\
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark240/spark-sql-proxy,\
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark210/spark-yarn-client,\
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark210/spark-sql-proxy,\
engine-worker/engine-plugins/spark/yarn2-hdfs2-spark210/spark-yarn-client,\
engine-worker/engine-plugins/spark/yarn2-hdfs2-spark210/spark-sql-proxy,\
engine-worker/engine-plugins/dtscript/yarn3-hdfs3-dtscript/dtscript-client,\
engine-worker/engine-plugins/dtscript/yarn2-hdfs2-dtscript/dtscript-client,\
engine-worker/engine-plugins/hadoop/yarn3-hdfs3-hadoop3,\
engine-worker/engine-plugins/hadoop/yarn2-hdfs2-hadoop2,\
engine-worker/engine-plugins/stores/hdfs2,\
engine-worker/engine-plugins/stores/hdfs3,\
engine-worker/engine-plugins/schedules/yarn2,\
engine-worker/engine-plugins/schedules/yarn3,\
engine-worker/engine-plugins/rdbs/hive,\
engine-worker/engine-plugins/rdbs/hive2,\
engine-worker/engine-plugins/rdbs/hive3,\
-am