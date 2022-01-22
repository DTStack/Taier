### 对接过的环境

####  HDP3.1.4
- 增加Spark配置项

 - spark.sql.hive.convertMetastoreOrc=true
 - spark.sql.catalogImplementation=hive
 - spark.sql.hive.metastore.version=3.0
 // 指定一个路径，jar包上传到Sparkjars，或者只配置路径即可
 spark.sql.hive.metastore.jars=/tmp/（
 //存储standalone-metastore-1.21.2.3.1.4.0-315-hive3.jar）