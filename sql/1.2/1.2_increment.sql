-- ----------------------------
-- console component config add tip
-- ----------------------------
BEGIN;
TRUNCATE TABLE DICT;
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('spark_version', '2.1', '210', null, 2, 1, 'INTEGER', '', 1, '2021-03-02 14:15:23', '2021-03-02 14:15:23', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('spark_thrift_version', '1.x', '1.x', null, 3, 1, 'STRING', '', 0, '2021-03-02 14:16:41', '2021-03-02 14:16:41',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('spark_thrift_version', '2.x', '2.x', null, 3, 2, 'STRING', '', 1, '2021-03-02 14:16:41', '2021-03-02 14:16:41',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_config', 'HDP 3.1.x', '-200', '', 5, 0, 'LONG', 'SPARK', 0, '2021-02-05 11:53:21',
        '2021-02-05 11:53:21', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'yarn3-hdfs3-spark210', '-108', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:23',
        '2021-03-04 17:50:23', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'yarn2-hdfs2-spark210', '-108', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24',
        '2021-03-04 17:50:24', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'dummy', '-101', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'hive', '-117', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'hive2', '-117', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'hive3', '-117', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'Apache Hadoop 2.x', '2.7.6', null, 0, 1, 'STRING', 'Apache Hadoop', 0, '2021-12-28 10:18:58',
        '2021-12-28 10:18:58', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'Apache Hadoop 3.x', '3.0.0', null, 0, 2, 'STRING', 'Apache Hadoop', 0, '2021-12-28 10:18:58',
        '2021-12-28 10:18:58', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'HDP 2.6.x', '2.7.3', null, 0, 1, 'STRING', 'HDP', 0, '2021-12-28 10:18:59',
        '2021-12-28 10:18:59', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'HDP 3.x', '3.1.1', null, 0, 2, 'STRING', 'HDP', 0, '2021-12-28 10:18:59',
        '2021-12-28 10:18:59', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'CDH 5.x', '2.3.0', null, 0, 1, 'STRING', 'CDH', 0, '2021-12-28 10:19:00',
        '2021-12-28 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'CDH 6.0.x', '3.0.0', null, 0, 11, 'STRING', 'CDH', 0, '2021-12-28 10:19:01',
        '2021-12-28 10:19:01', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'CDH 6.1.x', '3.0.0', null, 0, 12, 'STRING', 'CDH', 0, '2021-12-28 10:19:01',
        '2021-12-28 10:19:01', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'CDH 6.2.x', '3.0.0', null, 0, 13, 'STRING', 'CDH', 0, '2021-12-28 10:19:01',
        '2021-12-28 10:19:01', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'CDP 7.x', '3.1.1', null, 0, 15, 'STRING', 'CDH', 0, '2021-12-28 10:19:02',
        '2021-12-28 10:19:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'TDH 5.2.x', '2.7.0', null, 0, 1, 'STRING', 'TDH', 0, '2021-12-28 10:19:02',
        '2021-12-28 10:19:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'TDH 7.x', '2.7.0', null, 0, 2, 'STRING', 'TDH', 0, '2021-12-28 10:19:02',
        '2021-12-28 10:19:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'TDH 6.x', '2.7.0', null, 0, 1, 'STRING', 'TDH', 0, '2021-12-28 11:44:02',
        '2021-12-28 11:44:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'HDFS',
        '{"owner": "STORAGE", "dependsOn": ["RESOURCE"], "allowKerberos": "true", "allowCoexistence": false, "uploadConfigType": "1", "versionDictionary": "HADOOP_VERSION"}',
        null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'FLINK',
        '{"owner": "COMPUTE", "dependsOn": ["RESOURCE", "STORAGE"], "allowKerberos": "true", "allowCoexistence": true, "uploadConfigType": "0", "versionDictionary": "FLINK_VERSION"}',
        null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'SPARK',
        '{"owner": "COMPUTE", "dependsOn": ["RESOURCE", "STORAGE"], "allowKerberos": "true", "allowCoexistence": true, "uploadConfigType": "0", "versionDictionary": "SPARK_VERSION"}',
        null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-28 16:54:54', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'SPARK_THRIFT',
        '{"owner": "COMPUTE", "dependsOn": ["RESOURCE", "STORAGE"], "allowKerberos": "true", "allowCoexistence": false, "uploadConfigType": "0", "versionDictionary": "SPARK_THRIFT_VERSION"}',
        null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'HIVE_SERVER',
        '{"owner": "COMPUTE", "dependsOn": ["RESOURCE", "STORAGE"], "allowKerberos": "true", "allowCoexistence": false, "uploadConfigType": "0", "versionDictionary": "HIVE_VERSION"}',
        null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'SFTP',
        '{"owner": "COMMON", "dependsOn": [], "nameTemplate": "dummy", "allowKerberos": "false", "allowCoexistence": false, "uploadConfigType": "0"}',
        null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'YARN',
        '{"owner": "RESOURCE", "dependsOn": [], "allowKerberos": "true", "allowCoexistence": false, "uploadConfigType": "1", "versionDictionary": "HADOOP_VERSION"}',
        null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', '1.x', '{"1.x": "hive"}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0,
        '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', '2.x', '{"2.x": "hive2"}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0,
        '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', '3.x-apache', '{"3.x-apache": "hive3"}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0,
        '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', '3.x-cdp', '{"3.x-cdp": "hive3"}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0,
        '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', '1.x', '{"1.x": "hive"}', null, 14, 1, 'STRING', 'SPARK_THRIFT', 0,
        '2021-12-31 15:00:16', '2021-12-31 15:00:16', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', '2.x', '{"2.x": "hive2"}', null, 14, 1, 'STRING', 'SPARK_THRIFT', 0,
        '2021-12-31 15:00:16', '2021-12-31 15:00:16', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('SPARK_SQL', 'SPARK_SQL', '0', 'SparkSQL', 30, 1, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('SYNC', '数据同步', '2', '数据同步', 30, 5, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('VIRTUAL', '虚节点', '-1', '虚节点', 30, 11, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('ResourceManager', 'ResourceManager', '3', '资源管理', 31, 3, 'STRING', '', 1, '2022-02-11 10:40:14',
        '2022-02-11 10:40:14', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('SparkSQLFunction', 'SparkSQLFunction', '4', 'SparkSQL', 31, 4, 'STRING', '', 1, '2022-02-11 10:40:14',
        '2022-07-05 14:54:37', 1);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('TableQuery', 'TableQuery', '5', '表查询', 31, 5, 'STRING', '', 1, '2022-02-11 10:40:14', '2022-07-05 14:54:37',
        1);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('TaskDevelop', 'TaskDevelop', '1', '任务开发', 31, 1, 'STRING', '', 1, '2022-02-11 10:40:14',
        '2022-02-11 10:40:14', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('ResourceManager', 'ResourceManager', '3', '资源管理', 32, 3, 'STRING', '', 1, '2022-02-11 10:42:19',
        '2022-02-11 10:42:19', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('TaskManager', 'TaskManager', '1', '任务管理', 32, 1, 'STRING', '', 1, '2022-02-11 10:42:19',
        '2022-02-11 10:42:19', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('CustomFunction', 'CustomFunction', '6', '自定义函数', 33, 4, 'STRING', '', 1, '2022-02-11 10:42:57',
        '2022-02-11 10:42:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('SystemFunction', 'SystemFunction', '6', '系统函数', 33, 2, 'STRING', '', 1, '2022-02-11 10:42:57',
        '2022-02-11 10:42:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('flink_version', '1.12', '112', null, 1, 2, 'INTEGER', '', 0, '2022-05-03 22:13:12', '2022-05-03 22:13:12', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'Apache Hadoop 2.x',
        '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"1.12": "yarn2-hdfs2-flink112"}], "SPARK": [{"2.1": "yarn2-hdfs2-spark210", "2.4": "yarn2-hdfs2-spark240"}], "DT_SCRIPT": "yarn2-hdfs2-dtscript"}, "YARN": "yarn2"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:01:55', '2021-12-28 11:01:55', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'Apache Hadoop 3.x',
        '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"1.12": "yarn3-hdfs3-flink112"}], "SPARK": [{"2.1": "yarn3-hdfs3-spark210", "2.4": "yarn3-hdfs3-spark240"}], "DT_SCRIPT": "yarn3-hdfs3-dtscript"}, "YARN": "yarn3"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:03:45', '2021-12-28 11:03:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'HDP 3.0.x',
        '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"1.12": "yarn3-hdfs3-flink112"}], "SPARK": [{"2.1": "yarn3-hdfs3-spark210", "2.4": "yarn3-hdfs3-spark240"}], "DT_SCRIPT": "yarn3-hdfs3-dtscript"}, "YARN": "yarn3"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:23', '2021-12-28 11:04:23', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'CDH 6.0.x',
        '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"1.8": "yarn3-hdfs3-flink180"}, {"1.10": "yarn3-hdfs3-flink110"}, {"1.12": "yarn3-hdfs3-flink112"}], "SPARK": [{"2.1": "yarn3-hdfs3-spark210", "2.4": "yarn3-hdfs3-spark240"}], "DT_SCRIPT": "yarn3-hdfs3-dtscript"}, "YARN": "yarn3"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:40', '2021-12-28 11:04:40', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'CDH 6.1.x',
        '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"1.12": "yarn3-hdfs3-flink112"}], "SPARK": [{"2.1": "yarn3-hdfs3-spark210", "2.4": "yarn3-hdfs3-spark240"}], "DT_SCRIPT": "yarn3-hdfs3-dtscript"}, "YARN": "yarn3"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:55', '2021-12-28 11:04:55', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'CDH 6.2.x',
        '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "TONY": "yarn3-hdfs3-tony", "FLINK": [{"1.8": "yarn3-hdfs3-flink180"}, {"1.10": "yarn3-hdfs3-flink110"}, {"1.12": "yarn3-hdfs3-flink112"}], "SPARK": [{"2.1": "yarn3-hdfs3-spark210", "2.4(CDH 6.2)": "yarn3-hdfs3-spark240cdh620"}], "LEARNING": "yarn3-hdfs3-learning", "DT_SCRIPT": "yarn3-hdfs3-dtscript"}, "YARN": "yarn3"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:05:06', '2021-12-28 11:05:06', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'HDP 2.6.x',
        '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"1.12": "yarn2-hdfs2-flink112"}], "SPARK": [{"2.1": "yarn2-hdfs2-spark210", "2.4": "yarn2-hdfs2-spark240"}], "DT_SCRIPT": "yarn2-hdfs2-dtscript"}, "YARN": "yarn2"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:06:38', '2021-12-28 11:06:38', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'CDH 5.x',
        '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"1.12": "yarn2-hdfs2-flink112"}], "SPARK": [{"2.1": "yarn2-hdfs2-spark210", "2.4": "yarn2-hdfs2-spark240"}], "DT_SCRIPT": "yarn2-hdfs2-dtscript"}, "YARN": "yarn2"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:07:19', '2021-12-28 11:07:19', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'HDP 3.x',
        '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"1.12": "yarn3-hdfs3-flink112"}], "SPARK": [{"2.1": "yarn3-hdfs3-spark210", "2.4": "yarn3-hdfs3-spark240"}], "DT_SCRIPT": "yarn3-hdfs3-dtscript"}, "YARN": "yarn3"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:43:05', '2021-12-28 11:43:05', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'TDH 5.2.x',
        '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"1.12": "yarn2-hdfs2-flink112"}], "SPARK": [{"2.1": "yarn2-hdfs2-spark210", "2.4": "yarn2-hdfs2-spark240"}], "DT_SCRIPT": "yarn2-hdfs2-dtscript"}, "YARN": "yarn2"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:44:33', '2021-12-28 11:44:33', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'TDH 6.x',
        '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"1.12": "yarn2-hdfs2-flink112"}], "SPARK": [{"2.1": "yarn2-hdfs2-spark210", "2.4": "yarn2-hdfs2-spark240"}], "DT_SCRIPT": "yarn2-hdfs2-dtscript"}, "YARN": "yarn2"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:44:43', '2021-12-28 11:44:43', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'TDH 7.x',
        '{"HDFS": {"HDFS": "yarn2-hdfs2-hadoop2", "FLINK": [{"1.12": "yarn2-hdfs2-flink112"}], "SPARK": [{"2.1": "yarn2-hdfs2-spark210", "2.4": "yarn2-hdfs2-spark240"}], "DT_SCRIPT": "yarn2-hdfs2-dtscript"}, "YARN": "yarn2"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'CDP 7.x',
        '{"HDFS": {"HDFS": "yarn3-hdfs3-hadoop3", "FLINK": [{"1.12": "yarn3-hdfs3-flink112"}], "SPARK": [{"2.1": "yarn3-hdfs3-spark210", "2.4": "yarn3-hdfs3-spark240"}], "DT_SCRIPT": "yarn3-hdfs3-dtscript"}, "YARN": "yarn3"}',
        null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'yarn2-hdfs2-flink112', '-115', null, 6, 0, 'LONG', '', 0, '2021-05-18 11:29:00',
        '2021-05-18 11:29:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'yarn3-hdfs3-flink112', '-115', null, 6, 0, 'LONG', '', 0, '2021-05-18 11:29:00',
        '2021-05-18 11:29:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hive_version', '1.x', '1.x', null, 4, 1, 'STRING', '', 0, '2022-05-03 22:20:53', '2022-05-03 22:20:53', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hive_version', '2.x', '2.x', null, 4, 2, 'STRING', '', 1, '2022-05-03 22:20:54', '2022-05-03 22:20:54', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hive_version', '3.x-apache', '3.x-apache', null, 4, 3, 'STRING', '', 1, '2022-05-03 22:20:54',
        '2022-05-03 22:20:54', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('hive_version', '3.x-cdp', '3.x-cdp', null, 4, 3, 'STRING', '', 1, '2022-05-03 22:20:55', '2022-05-03 22:20:55',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('FlinkSQLFunction', 'FlinkSQLFunction', '4', 'FlinkSQL', 31, 4, 'STRING', '', 1, '2022-05-03 22:21:10',
        '2022-07-05 14:54:37', 1);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.submit.deployMode', 'spark driver的jvm扩展参数', '1', 25, 0, 'STRING', '主要', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'sparkPythonExtLibPath', '远程存储系统上pyspark.zip和py4j-0.10.7-src.zip的路径
注：pyspark.zip和py4j-0.10.7-src.zip在$SPARK_HOME/python/lib路径下获取', '1', 25, 0, 'STRING', '主要', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'sparkSqlProxyPath', '远程存储系统上spark-sql-proxy.jar路径
注：spark-sql-proxy.jar是用来执行spark sql的jar包', '1', 25, 0, 'STRING', '主要', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.yarn.maxAppAttempts', 'spark driver最大尝试次数, 默认为yarn上yarn.resourcemanager.am.max-attempts配置的值
注：如果spark.yarn.maxAppAttempts配置的大于yarn.resourcemanager.am.max-attempts则无效', '1', 25, 0, 'STRING', '主要', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'sparkYarnArchive', '远程存储系统上spark jars的路径', '1', 25, 0, 'STRING', '主要', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'yarnAccepterTaskNumber', '允许yarn上同时存在状态为accepter的任务数量，当达到这个值后会禁止任务提交',
        '1', 25, 0, 'STRING', '主要', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.speculation', 'spark任务推测行为', '1', 25, 0, 'STRING', '主要', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.executor.cores', '每个executor可以使用的cpu核数', '1', 25, 0, 'STRING', '资源', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.executor.memory', '每个executor可以使用的内存量', '1', 25, 0, 'STRING', '资源', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.executor.instances', 'executor实例数', '1', 25, 0, 'STRING', '资源', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.cores.max', ' standalone模式下任务最大能申请的cpu核数', '1', 25, 0, 'STRING', '资源', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.network.timeout', 'spark中所有网络交互的最大超时时间', '1', 25, 0, 'STRING', '网络', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.rpc.askTimeout', 'RPC 请求操作在超时之前等待的持续时间', '1', 25, 0, 'STRING', '网络', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.executor.heartbeatInterval', 'driver和executor之间心跳时间间隔', '1', 25, 0, 'STRING', '网络', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.eventLog.compress', '是否对spark事件日志进行压缩', '1', 25, 0, 'STRING', '事件日志', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.eventLog.dir', 'spark事件日志存放路径', '1', 25, 0, 'STRING', '事件日志', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.eventLog.enabled', '是否记录 spark 事件日志', '1', 25, 0, 'STRING', '事件日志', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.driver.extraJavaOptions', 'spark driver的jvm扩展参数', '1', 25, 0, 'STRING', 'JVM', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.executor.extraJavaOptions', 'spark executor的jvm扩展参数', '1', 25, 0, 'STRING', 'JVM', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON',
        'driver中用于执行pyspark任务的python二进制可执行文件路径', '1', 25, 0, 'STRING', '环境变量', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'spark.yarn.appMasterEnv.PYSPARK_PYTHON', '用于执行pyspark任务的python二进制可执行文件路径', '1', 25, 0,
        'STRING', '环境变量', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'jobmanager.memory.process.size', 'JobManager 总内存(master)', '0', 25, 0, 'STRING', '公共参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'taskmanager.memory.process.size', 'TaskManager 总内存(slaves)', '0', 25, 0, 'STRING', '公共参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'taskmanager.numberOfTaskSlots', '单个 TaskManager 可以运行的并行算子或用户函数实例的数量。', '0', 25, 0,
        'STRING', '公共参数', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'high-availability', 'flink ha类型', '0', 25, 0, 'STRING', '高可用', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'high-availability.zookeeper.quorum', 'zookeeper地址，当ha选择是zookeeper时必填', '0', 25, 0, 'STRING',
        '高可用', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'high-availability.zookeeper.path.root', 'ha节点路径', '0', 25, 0, 'STRING', '高可用', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'high-availability.storageDir', 'ha元数据存储路径', '0', 25, 0, 'STRING', '高可用', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'prometheusHost', 'prometheus地址，平台端使用', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'prometheusPort', 'prometheus，平台端使用', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'metrics.reporter.promgateway.class', '用来推送指标类', '0', 25, 0, 'STRING', 'metric监控', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'metrics.reporter.promgateway.host', 'promgateway地址', '0', 25, 0, 'STRING', 'metric监控', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'metrics.reporter.promgateway.port', 'promgateway端口', '0', 25, 0, 'STRING', 'metric监控', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'metrics.reporter.promgateway.deleteOnShutdown', '任务结束后是否删除指标', '0', 25, 0, 'STRING',
        'metric监控', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'metrics.reporter.promgateway.jobName', '指标任务名', '0', 25, 0, 'STRING', 'metric监控', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'metrics.reporter.promgateway.randomJobNameSuffix', '是否在任务名上添加随机值', '0', 25, 0, 'STRING',
        'metric监控', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'state.backend', '状态后端', '0', 25, 0, 'STRING', '容错和checkpointing', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'state.backend.incremental', '是否开启增量', '0', 25, 0, 'STRING', '容错和checkpointing', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'state.checkpoints.dir', 'checkpoint路径地址', '0', 25, 0, 'STRING', '容错和checkpointing', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'state.checkpoints.num-retained', 'checkpoint保存个数', '0', 25, 0, 'STRING', '容错和checkpointing', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'state.savepoints.dir', 'savepoint路径', '0', 25, 0, 'STRING', '容错和checkpointing', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'checkpoint.retain.time', '检查点保留时间', '0', 25, 0, 'STRING', '容错和checkpointing', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'classloader.resolve-order', '类加载模式', '0', 25, 0, 'STRING', '高级', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'jobmanager.archive.fs.dir', '任务结束后任务信息存储路径', '0', 25, 0, 'STRING', '高级', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'akka.ask.timeout', 'akka通讯超时时间', '0', 25, 0, 'STRING', '高级', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'akka.tcp.timeout', 'tcp 连接的超时时间', '0', 25, 0, 'STRING', '高级', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'env.java.opts', 'jvm参数', '0', 25, 0, 'STRING', 'JVM参数', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'yarn.application-attempt-failures-validity-interval',
        '以毫秒为单位的时间窗口，它定义了重新启动 AM 时应用程序尝试失败的次数。不在此窗口范围内的故障不予考虑。将此值设置为 -1 以便全局计数。',
        '0', 25, 0, 'STRING', 'Yarn', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'yarn.application-attempts',
        'ApplicationMaster 重新启动的次数。默认情况下，该值将设置为 1。如果启用了高可用性，则默认值为 2。重启次数也受 YARN 限制（通过 yarn.resourcemanager.am.max-attempts 配置）。注意整个 Flink 集群会重启，YARN Client 会失去连接。',
        '0', 25, 0, 'STRING', 'Yarn', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'pluginLoadMode', '插件加载类型', '0', 25, 0, 'STRING', '数栈平台参数', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'classloader.dtstack-cache', '是否缓存classloader', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'sessionStartAuto', '是否允许engine启动flink session', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'checkSubmitJobGraphInterval', 'session check间隔（60 * 10s）', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'flinkLibDir', 'session check间隔（60 * 10s）', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'chunjunDistDir', 'flinkx plugins父级本地目录', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'remoteFlinkLibDir', 'flink lib 远程路径', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'remoteChunjunDistDir', 'flinkx plugins父级远程目录', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'flinkSessionName', 'yarn session名称', '0', 25, 0, 'STRING', '数栈平台参数', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'monitorAcceptedApp', '是否监控yarn accepted状态任务', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'yarnAccepterTaskNumber', '允许yarn accepter任务数量，达到这个值后不允许任务提交', '0', 25, 0, 'STRING',
        '数栈平台参数', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'slotmanager.number-of-slots.max', 'flink session允许的最大slot数', '0', 25, 0, 'STRING', '公共参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'sessionRetryNum', 'session重试次数，达到后会放缓重试的频率', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'restart-strategy',
        'none, off, disable:无重启策略。Fixed -delay, Fixed -delay:固定延迟重启策略。更多细节可以在这里找到。Failure -rate:故障率重启策略。更多细节可以在这里找到。如果检查点被禁用，默认值为none。如果检查点启用，默认值是fixed-delay with Integer。MAX_VALUE重启尝试和''1 s''延迟。',
        '0', 25, 0, 'STRING', '容错和checkpointing', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'restart-strategy.failure-rate.delay',
        '如果restart-strategy设置为根据失败率重试，则两次连续重启尝试之间的延迟。可以用“1分钟”、“20秒”来表示', '0', 25, 0,
        'STRING', '容错和checkpointing', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'clusterMode', '任务执行模式：perjob,session', '0', 25, 0, 'STRING', '数栈平台参数', 0,
        '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'restart-strategy.failure-rate.failure-rate-interval',
        '如果重启策略设置为故障率，测量故障率的时间间隔。可以用“1分钟”、“20秒”来表示。', '0', 25, 0, 'STRING',
        '容错和checkpointing', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'restart-strategy.failure-rate.max-failures-per-interval',
        '如果restart-strategy设置为根据失败率重试，在给定的时间间隔内，任务失败前的最大重启次数。', '0', 25, 0, 'STRING',
        '容错和checkpointing', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'jdbcUrl', 'jdbc url地址', '4', 25, 0, 'STRING', '', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'jdbcUrl', 'jdbc url地址', '5', 25, 0, 'STRING', '', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'username', 'jdbc连接用户名', '4', 25, 0, 'STRING', '', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'username', 'jdbc连接用户名', '5', 25, 0, 'STRING', '', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'password', 'jdbc连接密码', '4', 25, 0, 'STRING', '', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'password', 'jdbc连接密码', '5', 25, 0, 'STRING', '', 0, '2022-06-08 20:18:44', '2022-06-08 20:18:44',
        0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'maxJobPoolSize', '任务最大线程数', '4', 25, 0, 'STRING', '', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'maxJobPoolSize', '任务最大线程数', '5', 25, 0, 'STRING', '', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'minJobPoolSize', '任务最小线程数', '4', 25, 0, 'STRING', '', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'minJobPoolSize', '任务最小线程数', '5', 25, 0, 'STRING', '', 0, '2022-06-08 20:18:44',
        '2022-06-08 20:18:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('FunctionManager', 'FunctionManager', '4', '函数管理', 31, 2, 'STRING', '', 1, '2022-07-05 14:56:43',
        '2022-07-05 14:56:43', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('FunctionManager', 'FunctionManager', '4', '函数管理', 32, 4, 'STRING', '', 1, '2022-07-05 15:11:21',
        '2022-07-05 15:11:21', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'OCEAN_BASE',
        '{"owner": "COMPUTE", "dependsOn": [], "allowKerberos": "false", "allowCoexistence": false, "uploadConfigType": "0", "versionDictionary": "","nameTemplate":"oceanBase"}',
        null, 12, 0, 'STRING', '', 0, '2022-07-06 17:17:03', '2022-07-06 17:17:03', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'oceanBase', '-118', null, 6, 0, 'LONG', '', 0, '2022-07-06 19:32:06',
        '2022-07-06 19:32:06', 0);

-- console model
update console_component_config
set component_type_code = 6
where component_id = -101;

UPDATE console_component_config
SET value = 'perjob'
WHERE component_id = -108;

DELETE
FROM console_component_config
WHERE component_id = (-117, -118, -101);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -117, 5, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -117, 5, 'INPUT', 0, 'username', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -117, 5, 'PASSWORD', 0, 'password', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -117, 5, 'INPUT', 0, 'queue', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -117, 5, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -117, 5, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, now(), now(), 0);


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'host', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'RADIO_LINKAGE', 1, 'auth', '1', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, '', 1, 'password', '1', null, 'auth', '1', null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'username', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'PASSWORD', 1, 'password', '', null, 'auth$password', '', null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'port', '22', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'path', '/data/sftp', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, '', 1, 'rsaPath', '2', null, 'auth', '2', null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'input', 1, 'rsaPath', '', null, 'auth$rsaPath', '', null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'fileTimeout', '300000', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'isUsePool', 'true', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'maxIdle', '16', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'maxTotal', '16', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'maxWaitMillis', '3600000', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'minIdle', '16', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -101, 6, 'INPUT', 1, 'timeout', '10000', null, null, null, null, now(), now(), 0);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -118, 5, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -118, 5, 'INPUT', 0, 'username', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -118, 5, 'PASSWORD', 0, 'password', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -118, 5, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -118, 5, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, now(), now(), 0);

--
UPDATE console_component_config
SET component_type_code = 6
WHERE component_id = -101;

DELETE
FROM console_component_config
WHERE component_id IN (-115, -108);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'CHECKBOX', 1, 'deploymode', '["perjob","session"]', null, '', '', null, '2021-07-27 13:52:46',
        '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'GROUP', 1, 'perjob', 'perjob', null, 'deploymode', 'perjob', null, '2021-07-27 13:52:46',
        '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'akka.ask.timeout', '60 s', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'classloader.resolve-order', 'child-first', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'clusterMode', 'perjob', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46',
        '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'env.java.opts',
        '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8',
        null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin1.12/flink_lib', null, 'deploymode$perjob', null,
        null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'chunjunDistDir', '/data/insight_plugin1.12/chunjunplugin', null, 'deploymode$perjob',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'jobmanager.memory.process.size', '1600m', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'high-availability', 'ZOOKEEPER', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'taskmanager.memory.process.size', '2048m', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink112/ha', null,
        'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink112', null, 'deploymode$perjob', null,
        null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'taskmanager.numberOfTaskSlots', '1', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink112/completed-jobs', null,
        'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class',
        'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$perjob',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '112job', null, 'deploymode$perjob', null,
        null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, 'deploymode$perjob',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, '', 1, 'false', 'false', null,
        'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-07-27 13:52:46',
        '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'pluginLoadMode', 'shipfile', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'prometheusHost', '', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46',
        '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'prometheusPort', '9090', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46',
        '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'state.backend.incremental', 'true', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink112/checkpoints', null,
        'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink112/savepoints', null,
        'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'yarn.application-attempt-failures-validity-interval', '3600000', null,
        'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'yarn.application-attempts', '3', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'GROUP', 1, 'session', 'session', null, 'deploymode', 'session', null, '2021-07-27 13:52:46',
        '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'checkSubmitJobGraphInterval', '60', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'classloader.resolve-order', 'parent-first', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'clusterMode', 'session', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'env.java.opts',
        '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8',
        null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin1.12/flink_lib', null, 'deploymode$session', null,
        null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'chunjunDistDir', '/data/insight_plugin1.12/chunjunplugin', null, 'deploymode$session',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'slotmanager.number-of-slots.max', '10', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'jobmanager.memory.process.size', '1600m', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'high-availability', 'NONE', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'taskmanager.memory.process.size', '2048m', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink112/ha', null,
        'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink112', null, 'deploymode$session', null,
        null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'high-availability.zookeeper.quorum', '', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink112/completed-jobs', null,
        'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class',
        'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$session',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, '', 1, 'false', 'false', null, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, '', 1, 'true', 'true', null, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '112job', null, 'deploymode$session', null,
        null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null,
        'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, '', 1, 'false', 'false', null,
        'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-07-27 13:52:46',
        '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, '', 1, 'true', 'true', null, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix',
        null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'pluginLoadMode', 'shipfile', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'prometheusHost', '', null, 'deploymode$session', null, null, '2021-07-27 13:52:46',
        '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'prometheusPort', '9090', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'sessionRetryNum', '5', null, 'deploymode$session', null, null, '2021-07-27 13:52:46',
        '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'sessionStartAuto', 'true', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'state.backend.incremental', 'true', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink112/checkpoints', null,
        'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink112/savepoints', null,
        'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'taskmanager.numberOfTaskSlots', '1', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'yarn.application-attempt-failures-validity-interval', '3600000', null,
        'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'yarn.application-attempts', '3', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'remoteChunjunDistDir', '/data/insight_plugin1.12/chunjunplugin', null,
        'deploymode$perjob', null, null, '2021-07-27 13:56:15', '2021-07-27 13:56:15', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'akka.tcp.timeout', '60 s', null, 'deploymode$perjob', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'remoteChunjunDistDir', '/data/insight_plugin1.12/chunjunplugin', null,
        'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'flinkSessionName', 'flink_session', null, 'deploymode$session', null, null,
        '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'checkpoint.retain.time', '7', null, 'deploymode$perjob', null, null,
        '2021-08-24 17:22:06', '2021-08-24 17:22:06', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'remoteFlinkLibDir', '/data/insight_plugin1.12/flink_lib', null, 'deploymode$perjob',
        null, null, '2021-08-24 20:40:39', '2021-08-24 20:40:39', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 1, 'remoteFlinkLibDir', '/data/insight_plugin1.12/flink_lib', null, 'deploymode$session',
        null, null, '2021-08-24 20:41:46', '2021-08-24 20:41:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'restart-strategy', 'failure-rate', null, 'deploymode$perjob', null, null,
        '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'restart-strategy.failure-rate.delay', '10s', null, 'deploymode$perjob', null, null,
        '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'restart-strategy.failure-rate.failure-rate-interval', '5 min', null,
        'deploymode$perjob', null, null, '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -115, 0, 'INPUT', 0, 'restart-strategy.failure-rate.max-failures-per-interval', '3', null,
        'deploymode$perjob', null, null, '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'CHECKBOX', 1, 'deploymode', '["perjob"]', null, '', '', null, '2021-02-25 18:12:53',
        '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'GROUP', 1, 'perjob', 'perjob', null, 'deploymode', 'perjob', null, '2021-02-25 18:12:53',
        '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 0, 'addColumnSupport', 'true', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'spark.cores.max', '1', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53',
        '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 0, 'spark.driver.extraJavaOptions', '-Dfile.encoding=utf-8', null, 'deploymode$perjob',
        null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 0, 'spark.eventLog.compress', 'true', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 0, 'spark.eventLog.dir', 'hdfs://ns1/tmp/spark-yarn-logs', null, 'deploymode$perjob',
        null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 0, 'spark.eventLog.enabled', 'true', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'spark.executor.cores', '1', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 0, 'spark.executor.extraJavaOptions', '-Dfile.encoding=utf-8', null, 'deploymode$perjob',
        null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'spark.executor.heartbeatInterval', '600s', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'spark.executor.instances', '1', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'spark.executor.memory', '512m', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'spark.network.timeout', '600s', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'spark.rpc.askTimeout', '600s', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'spark.speculation', 'true', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'spark.submit.deployMode', 'cluster', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 0, 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON', '/data/miniconda2/bin/python3', null,
        'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 0, 'spark.yarn.appMasterEnv.PYSPARK_PYTHON', '/data/miniconda2/bin/python3', null,
        'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'spark.yarn.maxAppAttempts', '1', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'sparkPythonExtLibPath',
        'hdfs://ns1/dtInsight/pythons/pyspark.zip,hdfs://ns1/dtInsight/pythons/py4j-0.10.7-src.zip', null,
        'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'sparkSqlProxyPath', 'hdfs://ns1/dtInsight/spark/spark-sql-proxy.jar', null,
        'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 1, 'sparkYarnArchive', 'hdfs://ns1/dtInsight/sparkjars/jars', null, 'deploymode$perjob',
        null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -108, 1, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$perjob', null, null,
        '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);

alter table console_cluster_tenant
    add queue_name varchar(32) comment '队列名称';
update console_cluster_tenant
    inner join console_queue
on queue_id = console_queue.id
    set console_cluster_tenant.queue_name = console_queue.queue_name;

alter table console_cluster_tenant drop column queue_id;


CREATE TABLE `task_dirty_data_manage`
(
    `id`                      int(11) NOT NULL AUTO_INCREMENT,
    `task_id`                 int(11) NOT NULL COMMENT '任务id',
    `output_type`             varchar(25) NOT NULL COMMENT '输出类型1.log2.jdbc',
    `max_rows`                int(11) NOT NULL COMMENT '脏数据最大值',
    `max_collect_failed_rows` int(11) NOT NULL COMMENT '失败条数',
    `link_info`               text        NOT NULL COMMENT '连接信息json',
    `log_print_interval`      int(11) NOT NULL DEFAULT '0' COMMENT '日志打印频率',
    `tenant_id`               int(11) NOT NULL COMMENT '租户id',
    `gmt_create`              datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`            datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`              tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `index_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO task_template (task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted)
VALUES (11, 0, '1.12', '## 资源相关
parallelism.default=1
taskmanager.numberOfTaskSlots=1
jobmanager.memory.process.size=1g
taskmanager.memory.process.size=2g', now(), now(), 0);


COMMIT;