echo 'Dependency Hadoop-2.7.3 Building...'

mvn clean package -DskipTests -Dhadoop.version=2.7.3 -pl \
plugins/flink/flink150-hadoop2,\
plugins/flink/flink180-hadoop2,\
plugins/spark/spark-hadoop2,\
plugins/spark/spark-yarn-hadoop2,\
plugins/dtscript/dtscript-hadoop2/dtscript-client,\
plugins/learning/learning-hadoop2/learning-client,\
plugins/hadoop,\
plugins/kylin,\
plugins/rdbs,\
plugins/odps,\
entrance \
-am