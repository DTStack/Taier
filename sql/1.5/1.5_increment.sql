DELETE FROM console_component_config WHERE component_id = -109;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'CHECKBOX', 1, 'deploymode', '["perjob"]', null, '', '', null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'GROUP', 1, 'perjob', 'perjob', null, 'deploymode', 'perjob', null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 0, 'addColumnSupport', 'true', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.cores.max', '1', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 0, 'spark.driver.extraJavaOptions', '-Dfile.encoding=utf-8', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 0, 'spark.eventLog.compress', 'true', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 0, 'spark.eventLog.dir', 'hdfs:///tmp/spark-yarn-logs', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 0, 'spark.eventLog.enabled', 'true', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.executor.cores', '1', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 0, 'spark.executor.extraJavaOptions', '-Dfile.encoding=utf-8', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.executor.heartbeatInterval', '10s', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.executor.instances', '1', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.executor.memory', '512m', null, 'deploymode$perjob', null, null, now(), now(), 0);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.sql.adaptive.advisoryPartitionSizeInBytes', '64MB', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.sql.adaptive.coalescePartitions.minPartitionSize', '1MB', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.sql.adaptive.coalescePartitions.initialPartitionNum', '200', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.sql.adaptive.skewJoin.skewedPartitionThresholdInBytes', '256MB', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.sql.adaptive.skewJoin.skewedPartitionFactor', '5', null, 'deploymode$perjob', null, null, now(), now(), 0);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.network.timeout', '600s', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.rpc.askTimeout', '600s', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.speculation', 'true', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.submit.deployMode', 'cluster', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 0, 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON', '/data/miniconda2/bin/python3', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 0, 'spark.yarn.appMasterEnv.PYSPARK_PYTHON', '/data/miniconda2/bin/python3', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'spark.yarn.maxAppAttempts', '1', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'sparkPythonExtLibPath', 'hdfs:///dtInsight/pythons/pyspark.zip,hdfs:///dtInsight/pythons/py4j-0.10.7-src.zip', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'sparkSqlProxyPath', 'hdfs:///dtInsight/spark/spark-sql-proxy.jar', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 1, 'sparkYarnArchive', 'hdfs:///dtInsight/sparkjars/jars', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 1, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$perjob', null, null, now(), now(), 0);

DELETE FROM dict WHERE dict_code = 'typename_mapping' AND dict_name IN ('yarn2-hdfs2-spark320','yarn3-hdfs3-spark320');

INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'yarn2-hdfs2-spark320', '-109', null, 6, 0, 'LONG', '', 0, now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'yarn3-hdfs3-spark320', '-109', null, 6, 0, 'LONG', '', 0, now(),now(), 0);

DELETE FROM dict WHERE dict_code = 'component_model_config' AND depend_name = 'YARN';
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'Apache Hadoop 2.x', '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"112": "yarn2-hdfs2-flink112"}], "SPARK": [{"320": "yarn2-hdfs2-spark320"}, {"210": "yarn2-hdfs2-spark210"], "SCRIPT": "yarn2-hdfs2-script"}, "YARN": "yarn2"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'Apache Hadoop 3.x', '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"112": "yarn3-hdfs3-flink112"}], "SPARK": [{"320": "yarn3-hdfs3-spark320"}, {"210": "yarn3-hdfs3-spark210"}], "SCRIPT": "yarn3-hdfs3-script"}, "YARN": "yarn3"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 5.x', '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"112": "yarn2-hdfs2-flink112"}], "SPARK": [{"320": "yarn2-hdfs2-spark320"}, {"210": "yarn2-hdfs2-spark210"}], "SCRIPT": "yarn2-hdfs2-script"}, "YARN": "yarn2"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.0.x', '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"112": "yarn3-hdfs3-flink112"}], "SPARK": [{"320": "yarn3-hdfs3-spark320"}, {"210": "yarn3-hdfs3-spark210"}], "SCRIPT": "yarn3-hdfs3-script"}, "YARN": "yarn3"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.1.x', '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"112": "yarn3-hdfs3-flink112"}], "SPARK": [{"320": "yarn3-hdfs3-spark320"}, {"210": "yarn3-hdfs3-spark210"}], "SCRIPT": "yarn3-hdfs3-script"}, "YARN": "yarn3"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.2.x', '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"112": "yarn3-hdfs3-flink112"}], "SPARK": [{"320": "yarn3-hdfs3-spark320"}, {"210": "yarn3-hdfs3-spark210"], "SCRIPT": "yarn3-hdfs3-script"}, "YARN": "yarn3"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDP 7.x', '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"112": "yarn3-hdfs3-flink112"}], "SPARK": [{"320": "yarn3-hdfs3-spark320"}, {"210": "yarn3-hdfs3-spark210"}], "SCRIPT": "yarn3-hdfs3-script"}, "YARN": "yarn3"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 2.6.x', '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"112": "yarn2-hdfs2-flink112"}], "SPARK": [{"320": "yarn2-hdfs2-spark320"}, {"210": "yarn2-hdfs2-spark210"}], "SCRIPT": "yarn2-hdfs2-script"}, "YARN": "yarn2"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 3.0.x', '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"112": "yarn3-hdfs3-flink112"}], "SPARK": [{"320": "yarn3-hdfs3-spark320"}, {"210": "yarn3-hdfs3-spark210"}], "SCRIPT": "yarn3-hdfs3-script"}, "YARN": "yarn3"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 3.x', '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"112": "yarn3-hdfs3-flink112"}], "SPARK": [{"320": "yarn3-hdfs3-spark320"}, {"210": "yarn3-hdfs3-spark210"}], "SCRIPT": "yarn3-hdfs3-script"}, "YARN": "yarn3"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 5.2.x', '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"112": "yarn2-hdfs2-flink112"}], "SPARK": [{"320": "yarn2-hdfs2-spark320"}, {"210": "yarn2-hdfs2-spark210"}], "SCRIPT": "yarn2-hdfs2-script"}, "YARN": "yarn2"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 6.x', '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"112": "yarn2-hdfs2-flink112"}], "SPARK": [{"320": "yarn2-hdfs2-spark320"}, {"210": "yarn2-hdfs2-spark210"}], "SCRIPT": "yarn2-hdfs2-script"}, "YARN": "yarn2"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 7.x', '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"112": "yarn2-hdfs2-flink112"}], "SPARK": [{"320": "yarn2-hdfs2-spark320"}, {"210": "yarn2-hdfs2-spark210"}], "SCRIPT": "yarn2-hdfs2-script"}, "YARN": "yarn2"}', null, 14, 1, 'STRING', 'YARN', 0,  now(),now(), 0);



INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('spark_version', '3.2', '320', null, 2, 1, 'INTEGER', '', 1, now(),now(), 0);

UPDATE dict set dict_value = '{"actions": ["SAVE_TASK", "RUN_TASK", "STOP_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "barItem": ["task", "dependency", "task_params", "env_params"], "formField": ["datasource","queue","componentVersion"], "renderKind": "editor","dataTypeCodes":["45"]}'
WHERE dict_code = 1 AND dict_name = 'SparkSQL';

DELETE FROM task_param_template WHERE task_name = 'SPARK_SQL' AND task_version = '3.2';

INSERT INTO task_param_template (task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES (0, 'SPARK_SQL', '3.2', '## Driver程序使用的CPU核数,默认为1
# spark.driver.cores=1

## Driver程序使用内存大小,默认1g
# spark.driver.memory=1g

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# spark.driver.maxResultSize=1g

## 启动的executor的数量，默认为1
# spark.executor.instances=1

## 每个executor使用的CPU核数，默认为1
# spark.executor.cores=1

## 每个executor内存大小,默认1g
# spark.executor.memory=1g

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead=

## 设置spark sql shuffle分区数，默认200
# spark.sql.shuffle.partitions=200

## 开启spark推测行为，默认false
# spark.speculation=false', now(), now(), 0);