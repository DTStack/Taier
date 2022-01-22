#!/usr/bin/env bash
echo 'Taiga Building...'

mvn clean package -DskipTests  -pl \
taiga-common,\
taiga-worker/taiga-plugins/dummy,\
taiga-worker/taiga-plugins/flink/common,\
taiga-worker/taiga-plugins/flink/yarn3-hdfs3-flink110,\
taiga-worker/taiga-plugins/flink/yarn2-hdfs2-flink110,\
taiga-worker/taiga-plugins/spark/yarn3-hdfs3-spark210/spark-yarn-client,\
taiga-worker/taiga-plugins/spark/yarn3-hdfs3-spark210/spark-sql-proxy,\
taiga-worker/taiga-plugins/spark/yarn2-hdfs2-spark210/spark-yarn-client,\
taiga-worker/taiga-plugins/spark/yarn2-hdfs2-spark210/spark-sql-proxy,\
taiga-worker/taiga-plugins/dtscript/yarn3-hdfs3-dtscript/dtscript-client,\
taiga-worker/taiga-plugins/dtscript/yarn2-hdfs2-dtscript/dtscript-client,\
taiga-worker/taiga-plugins/hadoop/yarn3-hdfs3-hadoop3,\
taiga-worker/taiga-plugins/hadoop/yarn2-hdfs2-hadoop2,\
taiga-worker/taiga-plugins/stores/hdfs2,\
taiga-worker/taiga-plugins/stores/hdfs3,\
taiga-worker/taiga-plugins/schedules/yarn2,\
taiga-worker/taiga-plugins/schedules/yarn3,\
taiga-worker/taiga-plugins/rdbs/hive,\
taiga-worker/taiga-plugins/rdbs/hive2,\
taiga-worker/taiga-plugins/rdbs/hive3,\
-am