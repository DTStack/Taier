@echo off
echo 'DAGSchedulex Building...'

set hadoop2Version=%1
if (%hadoop2Version%)==() (
   set hadoop2Version=2.7.3
)
echo "Hadoop2Version: %hadoop2Version% Building...""


set hadoop3Version=%2
if (%hadoop3Version%)==() (
    set hadoop3Version=3.0.0
)

echo "Hadoop3Version: %hadoop3Version% Building..."

mvn -T 12 clean package -DskipTests -Dhadoop2.version=%hadoop2Version% -Dhadoop3.version=%hadoop3Version% -pl ^
engine-worker/engine-plugins/dummy,^
engine-worker/engine-plugins/flink/common,^
engine-worker/engine-plugins/flink/yarn3-hdfs3-flink180,^
engine-worker/engine-plugins/flink/yarn2-hdfs2-flink180,^
engine-worker/engine-plugins/flink/yarnHW-hdfsHW-flink180HW,^
engine-worker/engine-plugins/flink/k8s-hdfs2-flink110,^
engine-worker/engine-plugins/flink/yarn3-hdfs3-flink110,^
engine-worker/engine-plugins/flink/yarn2-hdfs2-flink110,^
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark240/spark-yarn-client,^
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark240/spark-sql-proxy,^
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark210/spark-yarn-client,^
engine-worker/engine-plugins/spark/yarn3-hdfs3-spark210/spark-sql-proxy,^
engine-worker/engine-plugins/spark/yarn2-hdfs2-spark210/spark-yarn-client,^
engine-worker/engine-plugins/spark/yarn2-hdfs2-spark210/spark-sql-proxy,^
engine-worker/engine-plugins/spark/k8s-hdfs2-spark240/spark-k8s-client,^
engine-worker/engine-plugins/spark/k8s-hdfs2-spark240/spark-sql-proxy,^
engine-worker/engine-plugins/dtscript/yarn3-hdfs3-dtscript/dtscript-client,^
engine-worker/engine-plugins/dtscript/yarn2-hdfs2-dtscript/dtscript-client,^
engine-worker/engine-plugins/learning/yarn2-hdfs2-learning/learning-client,^
engine-worker/engine-plugins/learning/yarn3-hdfs3-learning/learning-client,^
engine-worker/engine-plugins/hadoop/yarn3-hdfs3-hadoop3,^
engine-worker/engine-plugins/hadoop/yarn2-hdfs2-hadoop2,^
engine-worker/engine-plugins/hadoop/k8s-hdfs2-hadoop2,^
engine-worker/engine-plugins/kylin,^
engine-worker/engine-plugins/odps,^
engine-worker/engine-plugins/rdbs/mysql,^
engine-worker/engine-plugins/rdbs/oracle,^
engine-worker/engine-plugins/rdbs/sqlserver,^
engine-worker/engine-plugins/rdbs/hive,^
engine-worker/engine-plugins/rdbs/hive2,^
engine-worker/engine-plugins/rdbs/hive-2.1.1-cdh6.1.1,^
engine-worker/engine-plugins/rdbs/postgresql,^
engine-worker/engine-plugins/rdbs/impala,^
engine-worker/engine-plugins/rdbs/tidb,^
engine-worker/engine-plugins/rdbs/greenplum,^
engine-worker/engine-plugins/rdbs/presto,^
engine-entrance ^
-am