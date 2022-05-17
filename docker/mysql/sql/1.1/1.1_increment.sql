DELETE FROM dict WHERE dict_code = 'flink_version' AND dict_value IN ('110','112');
DELETE FROM dict WHERE dict_code = 'typename_mapping' AND dict_value IN ('yarn2-hdfs2-flink110','yarn2-hdfs2-flink110');

DELETE FROM console_component_config WHERE cluster_id = -2 and component_type_code = 0;

INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('flink_version', '1.12', '112', null, 1, 2, 'INTEGER', '0,1,2', 0, now(),now(), 0);

DELETE FROM dict WHERE dict_code = 'component_model_config' AND depend_name = 'YARN'

INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'Apache Hadoop 2.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:01:55', '2021-12-28 11:01:55', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'Apache Hadoop 3.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:03:45', '2021-12-28 11:03:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 3.0.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:23', '2021-12-28 11:04:23', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.0.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {   "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:40', '2021-12-28 11:04:40', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.1.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:55', '2021-12-28 11:04:55', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.2.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4(CDH 6.2)":"yarn3-hdfs3-spark240cdh620"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:05:06', '2021-12-28 11:05:06', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 2.6.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:06:38', '2021-12-28 11:06:38', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 5.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:07:19', '2021-12-28 11:07:19', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 3.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:43:05', '2021-12-28 11:43:05', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 5.2.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:44:33', '2021-12-28 11:44:33', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 6.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:44:43', '2021-12-28 11:44:43', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 7.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDP 7.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);

INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'yarn2-hdfs2-flink112', '-115', null, 6, 0, 'LONG', '', 0, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'yarn3-hdfs3-flink112', '-115', null, 6, 0, 'LONG', '', 0, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'CHECKBOX', 1, 'deploymode', '["perjob","session"]', null, '', '', null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'GROUP', 1, 'perjob', 'perjob', null, 'deploymode', 'perjob', null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'akka.ask.timeout', '60 s', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'classloader.resolve-order', 'child-first', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'clusterMode', 'perjob', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin1.12/flink_lib', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'flinkxDistDir', '/data/insight_plugin1.12/flinkxplugin', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'jobmanager.memory.process.size', '1600m', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'high-availability', 'ZOOKEEPER', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'taskmanager.memory.process.size', '2048m', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink112/ha', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink112', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'taskmanager.numberOfTaskSlots', '1', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink112/completed-jobs', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '112job', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'pluginLoadMode', 'shipfile', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'prometheusHost', '', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'prometheusPort', '9090', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'state.backend.incremental', 'true', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink112/checkpoints', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink112/savepoints', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'yarn.application-attempt-failures-validity-interval', '3600000', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'yarn.application-attempts', '3', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'GROUP', 1, 'session', 'session', null, 'deploymode', 'session', null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'checkSubmitJobGraphInterval', '60', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'classloader.resolve-order', 'parent-first', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'clusterMode', 'session', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin1.12/flink_lib', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'flinkxDistDir', '/data/insight_plugin1.12/flinkxplugin', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'slotmanager.number-of-slots.max', '10', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'jobmanager.memory.process.size', '1600m', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'high-availability', 'NONE', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'taskmanager.memory.process.size', '2048m', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink112/ha', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink112', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'high-availability.zookeeper.quorum', '', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink112/completed-jobs', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'false', 'false', null, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'true', 'true', null, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '112job', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'false', 'false', null, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'true', 'true', null, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'pluginLoadMode', 'shipfile', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'prometheusHost', '', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'prometheusPort', '9090', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'sessionRetryNum', '5', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'sessionStartAuto', 'true', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'state.backend.incremental', 'true', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink112/checkpoints', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink112/savepoints', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'taskmanager.numberOfTaskSlots', '1', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'yarn.application-attempt-failures-validity-interval', '3600000', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'yarn.application-attempts', '3', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'remoteFlinkxDistDir', '/data/insight_plugin1.12/flinkxplugin', null, 'deploymode$perjob', null, null, '2021-07-27 13:56:15', '2021-07-27 13:56:15', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'akka.tcp.timeout', '60 s', null, 'deploymode$perjob', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'remoteFlinkxDistDir', '/data/insight_plugin1.12/flinkxplugin', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'flinkSessionName', 'flink_session', null, 'deploymode$session', null, null, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'checkpoint.retain.time', '7', null, 'deploymode$perjob', null, null, '2021-08-24 17:22:06', '2021-08-24 17:22:06', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'remoteFlinkLibDir', '/data/insight_plugin1.12/flink_lib', null, 'deploymode$perjob', null, null, '2021-08-24 20:40:39', '2021-08-24 20:40:39', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 1, 'remoteFlinkLibDir', '/data/insight_plugin1.12/flink_lib', null, 'deploymode$session', null, null, '2021-08-24 20:41:46', '2021-08-24 20:41:46', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'restart-strategy', 'failure-rate', null, 'deploymode$perjob', null, null, '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'restart-strategy.failure-rate.delay', '10s', null, 'deploymode$perjob', null, null, '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'restart-strategy.failure-rate.failure-rate-intervalattempts', '5 min', null, 'deploymode$perjob', null, null, '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, 'INPUT', 0, 'restart-strategy.failure-rate.max-failures-per-interval', '3', null, 'deploymode$perjob', null, null, '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);


-- auto-generated definition
create table schedule_job_history
(
    id              int auto_increment
        primary key,
    job_id          varchar(32)                          not null comment '工作任务id',
    exec_start_time datetime                             null comment '执行开始时间',
    exec_end_time   datetime                             null comment '执行结束时间',
    engine_job_id   varchar(256)                         null comment '额外id',
    application_id  varchar(256)                         null comment 'applicationId',
    gmt_create      datetime   default CURRENT_TIMESTAMP not null comment '新增时间',
    gmt_modified    datetime   default CURRENT_TIMESTAMP not null comment '修改时间',
    is_deleted      tinyint(1) default 0                 not null comment '0正常 1逻辑删除'
)
    charset = utf8;

create index index_engine_job_id
    on schedule_job_history (engine_job_id(128));

create index index_job_id
    on schedule_job_history (job_id, is_deleted);


ALTER TABLE `develop_task` ADD COLUMN `source_str` longtext COMMENT '输入源' AFTER `component_version`,
ADD COLUMN `target_str` longtext COMMENT '输出源' AFTER `source_str`,
ADD COLUMN `setting_str` longtext COMMENT '设置' AFTER `target_str`,
ADD COLUMN `create_model` tinyint COMMENT '任务模式 0 向导模式  1 脚本模式' AFTER `setting_str`,
ADD COLUMN `side_str` longtext COMMENT '维表' AFTER `target_str`,
ADD COLUMN `job_id` varchar(64) default null AFTER `side_str`
;


drop table if exists develop_task_template;
drop table if exists task_template;

CREATE TABLE `task_template` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `task_type` int(11) DEFAULT '0' COMMENT '任务类型',
                                 `type` int(11) DEFAULT NULL COMMENT '业务类型',
                                 `value_type` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '模版值类型',
                                 `content` text COLLATE utf8_bin COMMENT '模版值',
                                 `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8 COLLATE=utf8_bin

INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (1, 0, 1, '1', 'create table if not exists ods_order_header (
     order_header_id     string comment ''订单头id''
    ,order_date          bigint comment ''订单日期''
    ,shop_id             string comment ''店铺id''
    ,customer_id         string comment ''客户id''
    ,order_status        bigint comment ''订单状态''
    ,pay_date            bigint comment ''支付日期''

)comment ''销售订单明细表''
PARTITIONED BY (ds string) ;

create table if not exists ods_order_detail (
     order_header_id     string comment ''订单头id''
    ,order_detail_id     string comment ''订单明细id''
    ,item_id             string comment ''商品id''
    ,quantity            double comment ''商品数量''
    ,unit_price          double comment ''商品单价''
    ,dist_amout          double comment ''折扣金额''
)comment ''销售订单明细表''
PARTITIONED BY (ds string) ;


create table if not exists exam_ods_shop_info (
     shop_id                string comment ''店铺id''
    ,shop_name              string comment ''店铺名称''
    ,shop_type              string comment ''店铺类型''
    ,address                string comment ''店铺地址''
    ,status                 string comment ''店铺状态,open/closed''
)comment ''店铺维度表''
PARTITIONED BY (ds string) ;', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (3, 0, 1, '2', 'create table if not exists exam_dwd_sales_ord_df (
     order_header_id     string comment ''订单头id''
    ,order_detail_id     string comment ''订单明细id''
    ,order_date          bigint comment ''订单日期''
    ,pay_date            bigint comment ''付款日期''
    ,shop_id             string comment ''店铺id''
    ,customer_id         string comment ''客户id''
    ,item_id             string comment ''商品id''
    ,quantity            bigint comment ''商品数量''
    ,unit_price          double comment ''商品单价''
    ,amount              double comment ''总金额''
)comment ''销售订单明细表''
PARTITIONED BY (ds string) ;


INSERT OVERWRITE TABLE exam_dwd_sales_ord_df PARTITION(ds = ''${bdp.system.bizdate}'')
select
 d.order_header_id
,d.order_detail_id
,h.order_date
,h.pay_date
,h.shop_id
,h.customer_id
,d.item_id
,d.quantity
,d.unit_price
,d.quantity*d.unit_price-d.dist_amout as amount
from ods_order_header as h join ods_order_detail as d on h.order_header_id = d.order_header_id
where h.ds = ''${bdp.system.bizdate}'' and d.ds= ''${bdp.system.bizdate}''
and h.order_status = 0;
', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (5, 0, 1, '3', 'create table if not exists exam_dws_sales_shop_1d (
     stat_date              string comment ''统计日期''
    ,shop_id                string comment ''订单明细id''
    ,ord_quantity_1d        bigint comment ''最近一天订单数量''
    ,ord_amount_1d          double comment ''最近一天订单金额''
    ,pay_quantity_1d        bigint comment ''最近一天付款数量''
    ,pay_amount_1d          double comment ''最近一天付款金额''
)comment ''最近一天门店粒度销售汇总表''
PARTITIONED BY (ds string) ;

INSERT OVERWRITE TABLE exam_dws_sales_shop_1d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,shop_id
,sum(case when order_date = ''${bdp.system.bizdate}'' then quantity end) as ord_quantity_1d
,sum(case when order_date = ''${bdp.system.bizdate}'' then amount end)   as ord_amount_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then quantity end) as pay_quantity_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then amount end)   as pay_amount_1d
from
exam_dwd_sales_ord_df
where ds = ''${bdp.system.bizdate}''
group by shop_id;', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (7, 0, 1, '4', 'create table if not exists exam_ads_sales_all_d (
     stat_date              string comment ''统计日期''
    ,ord_quantity           bigint comment ''订单数量''
    ,ord_amount             double comment ''订单金额''
    ,pay_quantity           bigint comment ''付款数量''
    ,pay_amount             double comment ''付款金额''
    ,shop_cnt               bigint comment ''有交易的店铺数量''
)comment ''订单交易总表''
PARTITIONED BY (ds string) lifecycle 7;

INSERT OVERWRITE TABLE exam_ads_sales_all_d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,sum(ord_quantity_1d) as ord_quantity
,sum(ord_amount_1d)   as ord_amount
,sum(pay_quantity_1d) as pay_quantity
,sum(pay_amount_1d)   as pay_amount
,count(distinct shop_id) as shop_cnt
from
exam_dws_sales_shop_1d
where ds = ''${bdp.system.bizdate}''
group by shop_id;', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (9, 0, 1, '5', 'create table if not exists exam_dim_shop (
     shop_id                string comment ''店铺id''
    ,shop_name              string comment ''店铺名称''
    ,shop_type              string comment ''店铺类型''
    ,address                string comment ''店铺地址''
)comment ''店铺维度表''
PARTITIONED BY (ds string) lifecycle 365;

INSERT OVERWRITE TABLE exam_dim_shop PARTITION(ds = ''${bdp.system.bizdate}'')
select
 shop_id
,shop_name
,shop_type
,address
from exam_ods_shop_info
where ds = ''${bdp.system.bizdate}''
and status = ''open'';', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (11, 15, 1, '0', 'create table if not exists customer_base
(
    id                    varchar(20),
	cust_name             varchar(20),
	cust_phone            varchar(20),
	cust_wechat           varchar(20),
	cust_cefi_number      varchar(20),
	cust_car_number       varchar(20),
	cust_house_number     varchar(20),
	cust_job              varchar(20),
	cust_company          varchar(20),
	cust_work_address     varchar(20),
	cust_bank_number      varchar(20),
	cust_gate_card        varchar(20)
);
-- COMMENT ON customer_base IS ''用户基础表'';


create table if not exists customer_in_call
(
    id                        varchar,
    in_call_phone_number      varchar,
    in_call_time              varchar,--本来这个字段是时间戳类型的，但是libra不支持时间戳timestamp关键字，所以改成varchar类型
    in_call_duration          bigint,
    in_call_consult_problem   varchar
);
-- COMMENT ON customer_in_call IS ''用户来电记录表'';


create table if not exists customer_in_and_out
(
    id                      varchar,
    cust_gate_card          varchar,
    in_or_out               varchar,
    in_or_out_time          varchar
);
-- COMMENT ON customer_in_and_out IS ''客户出入记录表'';


create table if not exists customer_complain(
    id                        varchar,
    complain_phone            varchar,
    complain_name             varchar,
    complain_problem          varchar,
    complain_time             varchar
);

-- COMMENT ON customer_complain IS ''客户投诉记录表'';

-- --注意，Libra不支持时间戳类型的关键字
', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (13, 15, 1, '1', '
create table if not exists ods_order_header (
    order_header_id     varchar,
    order_date          bigint,
    shop_id             bigint,
    customer_id         varchar,
    order_status        bigint,
    pay_date            bigint
);

create table if not exists ods_order_detail (
    order_header_id     varchar,
    order_detail_id     varchar,
    item_id             varchar,
    quantity            varchar,
    unit_price          varchar,
    dist_amout          varchar
);

create table if not exists exam_ods_shop_info (
    shop_id                bigint,
    shop_name              varchar,
    shop_type              varchar,
    address                varchar,
    status                 varchar
);
', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (15, 15, 1, '2', '
create table if not exists exam_dwd_sales_ord_df (
    order_header_id     varchar,
    order_detail_id     varchar,
    order_date          bigint,
    pay_date            bigint,
    shop_id             bigint,
    customer_id         varchar,
    item_id             varchar,
    quantity            varchar,
    unit_price          varchar,
    amount              varchar
);

INSERT INTO exam_dwd_sales_ord_df
select
 d.order_header_id
,d.order_detail_id
,h.order_date
,h.pay_date
,h.shop_id
,h.customer_id
,d.item_id
,d.quantity
,d.unit_price
from ods_order_header as h join ods_order_detail as d
on h.order_header_id = d.order_header_id and h.order_status = 0;
', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (17, 15, 1, '3', 'create table if not exists exam_dws_sales_shop_1d (
     stat_date              string comment ''统计日期''
    ,shop_id                string comment ''订单明细id''
    ,ord_quantity_1d        bigint comment ''最近一天订单数量''
    ,ord_amount_1d          double comment ''最近一天订单金额''
    ,pay_quantity_1d        bigint comment ''最近一天付款数量''
    ,pay_amount_1d          double comment ''最近一天付款金额''
)comment ''最近一天门店粒度销售汇总表''
PARTITIONED BY (ds string) ;

INSERT OVERWRITE TABLE exam_dws_sales_shop_1d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,shop_id
,sum(case when order_date = ''${bdp.system.bizdate}'' then quantity end) as ord_quantity_1d
,sum(case when order_date = ''${bdp.system.bizdate}'' then amount end)   as ord_amount_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then quantity end) as pay_quantity_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then amount end)   as pay_amount_1d
from
exam_dwd_sales_ord_df
where ds = ''${bdp.system.bizdate}''
group by shop_id;
', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (19, 15, 1, '4', 'create table if not exists exam_ads_sales_all_d (
     stat_date              string comment ''统计日期''
    ,ord_quantity           bigint comment ''订单数量''
    ,ord_amount             double comment ''订单金额''
    ,pay_quantity           bigint comment ''付款数量''
    ,pay_amount             double comment ''付款金额''
    ,shop_cnt               bigint comment ''有交易的店铺数量''
)comment ''订单交易总表''
PARTITIONED BY (ds string) lifecycle 7;

INSERT OVERWRITE TABLE exam_ads_sales_all_d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,sum(ord_quantity_1d) as ord_quantity
,sum(ord_amount_1d)   as ord_amount
,sum(pay_quantity_1d) as pay_quantity
,sum(pay_amount_1d)   as pay_amount
,count(distinct shop_id) as shop_cnt
from
exam_dws_sales_shop_1d
where ds = ''${bdp.system.bizdate}''
group by shop_id;
', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (21, 15, 1, '5', 'create table if not exists exam_dim_shop (
     shop_id                string comment ''店铺id''
    ,shop_name              string comment ''店铺名称''
    ,shop_type              string comment ''店铺类型''
    ,address                string comment ''店铺地址''
)comment ''店铺维度表''
PARTITIONED BY (ds string) lifecycle 365;

INSERT OVERWRITE TABLE exam_dim_shop PARTITION(ds = ''${bdp.system.bizdate}'')
select
 shop_id
,shop_name
,shop_type
,address
from exam_ods_shop_info
where ds = ''${bdp.system.bizdate}''
and status = ''open'';
', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (31, 0, 0, '2.1', '## Driver程序使用的CPU核数,默认为1
# driver.cores=1

## Driver程序使用内存大小,默认512m
# driver.memory=512m

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# driver.maxResultSize=1g

## 启动的executor的数量，默认为1
executor.instances=1

## 每个executor使用的CPU核数，默认为1
executor.cores=1

## 每个executor内存大小,默认512m
executor.memory=512m

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (33, 2, 0, '1.10', '## 任务运行方式：
## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步
## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认per_job
## flinkTaskRunMode=per_job
## per_job模式下jobManager配置的内存大小，默认1024（单位M)
## jobmanager.memory.mb=1024
## per_job模式下taskManager配置的内存大小，默认1024（单位M）
## taskmanager.memory.mb=1024
## per_job模式下每个taskManager 对应 slot的数量
## slots=1
## checkpoint保存时间间隔
## flink.checkpoint.interval=300000
## 任务优先级, 范围:1-1000
## job.priority=10', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (35, 5, 0, '1.12', '## 资源相关
parallelism.default=1
taskmanager.numberOfTaskSlots=1
jobmanager.memory.process.size=1g
taskmanager.memory.process.size=2g

## 时间相关
## 设置Flink时间选项，有ProcessingTime,EventTime,IngestionTime可选
## 非脚本模式会根据Kafka自动设置。脚本模式默认为ProcessingTime
# pipeline.time-characteristic=EventTime

## Checkpoint相关
## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成
execution.checkpointing.interval=5min
## 状态恢复语义,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE
# execution.checkpointing.mode=EXACTLY_ONCE
##任务取消后保留hdfs上的checkpoint文件
execution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION

# Flink SQL独有，状态过期时间
table.exec.state.ttl=1d

log.level=INFO

## 使用Iceberg和Hive维表开启
# table.dynamic-table-options.enabled=true

## Kerberos相关
# security.kerberos.login.contexts=Client,KafkaClient


## 高阶参数
## 窗口提前触发时间
# table.exec.emit.early-fire.enabled=true
# table.exec.emit.early-fire.delay=1s

## 当一个源在超时时间内没有收到任何元素时，它将被标记为临时空闲
# table.exec.source.idle-timeout=10ms

## 是否开启minibatch
## 可以减少状态开销。这可能会增加一些延迟，因为它会缓冲一些记录而不是立即处理它们。这是吞吐量和延迟之间的权衡
# table.exec.mini-batch.enabled=true
## 状态缓存时间
# table.exec.mini-batch.allow-latency=5s
## 状态最大缓存条数
# table.exec.mini-batch.size=5000

## 是否开启Local-Global 聚合。前提需要开启minibatch
## 聚合是为解决数据倾斜问题提出的，类似于 MapReduce 中的 Combine + Reduce 模式
# table.optimizer.agg-phase-strategy=TWO_PHASE

## 是否开启拆分 distinct 聚合
## Local-Global 可以解决数据倾斜，但是在处理 distinct 聚合时，其性能并不令人满意。
## 如：SELECT day, COUNT(DISTINCT user_id) FROM T GROUP BY day 如果 distinct key （即 user_id）的值分布稀疏，建议开启
# table.optimizer.distinct-agg.split.enabled=true


## Flink算子chaining开关。默认为true。排查性能问题时会暂时设置成false，但降低性能。
# pipeline.operator-chaining=true', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (36, 6, 0, '1.12', '## 资源相关
parallelism.default=1
taskmanager.numberOfTaskSlots=1
jobmanager.memory.process.size=1g
taskmanager.memory.process.size=2g

## 时间相关
## 设置Flink时间选项，有ProcessingTime,EventTime,IngestionTime可选
## 非脚本模式会根据Kafka自动设置。脚本模式默认为ProcessingTime
# pipeline.time-characteristic=EventTime

## Checkpoint相关
## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成
execution.checkpointing.interval=5min
## 状态恢复语义,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE
# execution.checkpointing.mode=EXACTLY_ONCE
##任务取消后保留hdfs上的checkpoint文件
execution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION

# Flink SQL独有，状态过期时间
table.exec.state.ttl=1d

log.level=INFO

## 使用Iceberg和Hive维表开启
# table.dynamic-table-options.enabled=true

## Kerberos相关
# security.kerberos.login.contexts=Client,KafkaClient


## 高阶参数
## 窗口提前触发时间
# table.exec.emit.early-fire.enabled=true
# table.exec.emit.early-fire.delay=1s

## 当一个源在超时时间内没有收到任何元素时，它将被标记为临时空闲
# table.exec.source.idle-timeout=10ms

## 是否开启minibatch
## 可以减少状态开销。这可能会增加一些延迟，因为它会缓冲一些记录而不是立即处理它们。这是吞吐量和延迟之间的权衡
# table.exec.mini-batch.enabled=true
## 状态缓存时间
# table.exec.mini-batch.allow-latency=5s
## 状态最大缓存条数
# table.exec.mini-batch.size=5000

## 是否开启Local-Global 聚合。前提需要开启minibatch
## 聚合是为解决数据倾斜问题提出的，类似于 MapReduce 中的 Combine + Reduce 模式
# table.optimizer.agg-phase-strategy=TWO_PHASE

## 是否开启拆分 distinct 聚合
## Local-Global 可以解决数据倾斜，但是在处理 distinct 聚合时，其性能并不令人满意。
## 如：SELECT day, COUNT(DISTINCT user_id) FROM T GROUP BY day 如果 distinct key （即 user_id）的值分布稀疏，建议开启
# table.optimizer.distinct-agg.split.enabled=true


## Flink算子chaining开关。默认为true。排查性能问题时会暂时设置成false，但降低性能。
# pipeline.operator-chaining=true', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO task_template (id, task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (37, 17, 0, '', '## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复
#hiveconf:mapreduce.job.name=

## 指定mapreduce运行的队列，默认走控制台配置的queue
# hiveconf:mapreduce.job.queuename=default_queue_name

## hivevar配置,用户自定义变量
#hivevar:ageParams=30## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复
#hiveconf:mapreduce.job.name=

## 指定mapreduce运行的队列，默认走控制台配置的queue
# hiveconf:mapreduce.job.queuename=default_queue_name

## hivevar配置,用户自定义变量
#hivevar:ageParams=30', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);


INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                  depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('hive_version', '1.x', '1.x', null, 4, 1, 'STRING', '', 0, now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                  depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('hive_version', '2.x', '2.x', null, 4, 2, 'STRING', '', 1, now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                  depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('hive_version', '3.x-apache', '3.x-apache', null, 4, 3, 'STRING', '', 1, now(),now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                  depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('hive_version', '3.x-cdp', '3.x-cdp', null, 4, 3, 'STRING', '', 1, now(),now(), 0);


INSERT INTO task_template (task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES (7, 0, '', '## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复
#hiveconf:mapreduce.job.name=

## 指定mapreduce运行的队列，默认走控制台配置的queue
# hiveconf:mapreduce.job.queuename=default_queue_name

## hivevar配置,用户自定义变量
#hivevar:ageParams=30## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复
#hiveconf:mapreduce.job.name=

## 指定mapreduce运行的队列，默认走控制台配置的queue
# hiveconf:mapreduce.job.queuename=default_queue_name

## hivevar配置,用户自定义变量
#hivevar:ageParams=30', '2021-11-18 10:36:13', '2021-11-18 10:36:13', 0);




begin;
INSERT into develop_catalogue(tenant_id, node_name, node_pid, order_val, level, gmt_create, gmt_modified, create_user_id, is_deleted, catalogue_type) VALUES ( -1, 'Flink系统函数', 0, 3, 1, now(), now(), -1, 0, 0);
set @stream_sys_id=(select id from develop_catalogue where node_name='Flink系统函数');
INSERT into develop_catalogue(tenant_id, node_name, node_pid, order_val, level, gmt_create, gmt_modified, create_user_id, is_deleted, catalogue_type)
VALUES (-1,'数学函数',@stream_sys_id,3, 1, '2022-04-12 23:33:10', '2022-04-12 23:33:10', -1, 0, 0),
       (-1,'日期函数',@stream_sys_id,3, 1, '2022-04-12 23:33:10', '2022-04-12 23:33:10', -1, 0, 0),
       (-1,'字符函数',@stream_sys_id,3, 1, '2022-04-12 23:33:10', '2022-04-12 23:33:10', -1, 0, 0),
       (-1,'聚合函数',@stream_sys_id,3, 1, '2022-04-12 23:33:10', '2022-04-12 23:33:10', -1, 0, 0),
       (-1,'其它函数',@stream_sys_id,3, 1, '2022-04-12 23:33:10', '2022-04-12 23:33:10', -1, 0, 0);
commit;

ALTER TABLE `develop_function` ADD COLUMN `udf_type` int COMMENT '函数类型' AFTER `type`;

begin;
set @math=(select id from develop_catalogue where node_name='数学函数' and node_pid = (select id from develop_catalogue where node_name='Flink系统函数'));
INSERT INTO `develop_function`(name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, udf_type, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text)
VALUES ('POWER', 'null', '计算次幂', 'POWER(numeric1, numeric2)', '返回 numeric1 的 numeric2 次幂.', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('ABS', 'null', '计算numeric的绝对值', 'ABS(numeric)', '计算numeric的绝对值.', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('MOD', 'null', 'numeric1 对 numeric2 取模', 'MOD(numeric1, numeric2)', '返回numeric1除以numeric2的余数(模数). 仅当numeric1为负时结果为负.', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('SQRT', 'null', '计算平方根', 'SQRT(numeric)', '计算numeric平方根.', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('LN', 'null', '	计算自然数的对数', 'LN(numeric)', '返回numeric的自然对数(以e为底)', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('LOG10', 'null', '	返回数字10的对数', 'LOG10(numeric) ', '返回numeric的对数(以10为底)', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('EXP', 'null', '	计算自然指数的指数', 'EXP(numeric)', '返回自然对数e的numeric幂次方', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('CEIL', 'null', '向上取整', 'CEIL(numeric) or CEILING(numeric)', '求其不小于小给定实数的最小整数如：ceil(6.1)= ceil(6.9) = 7', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('FLOOR', 'null', '向下取整', 'FLOOR(numeric)', '求其不大于给定实数的最小整数如：FLOOR(6.1)= FLOOR(6.9) = 6', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('SIN', 'null', '计算正弦值', 'SIN(numeric)', '计算正弦值', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('COS', 'null', '计算余弦值', 'COS(numeric)', '计算余弦值', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('TAN', 'null', '计算正切值', 'TAN(numeric)', '计算正切值', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('COT', 'null', '计算余切值', 'COT(numeric)', '计算余切值', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('ASIN', 'null', '计算反正弦值', 'ASIN(numeric)', '计算反正弦值', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('ACOS', 'null', '计算反余弦值', 'ACOS(numeric)', '计算反余弦值', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('ATAN', 'null', '计算反正切值', 'ATAN(numeric)', '计算反正切值', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('DEGREES', 'null', '弧度值转换角度值', 'DEGREES(numeric)', '弧度值转换角度值', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('RADIANS', 'null', '将角度值转换成弧度值', 'RADIANS(numeric)', '将角度值转换成弧度值', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('SIGN', 'null', '计算数字的标志', 'SIGN(numeric)', '如果numeric是正数则返回1.0, 是负数则返回-1.0, 否则返回0.0 ', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('ROUND', 'null', '取近似值', 'ROUND(numeric, int)', '返回numeric的保留int位小数的近似值', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('PI', 'null', '取数学常数pi', 'PI()', '取数学常数pi', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('E', 'null', '取数学常数e', 'E()', '取数学常数e', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('RAND', 'null', '取随机数', 'RAND() or RAND(seed integer)', '返回一个0到1范围内的随机数,如果指定种子seed，则会等到一个稳定的随机数序列.', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('RAND_INTEGER', 'null', '取随机数', 'RAND_INTEGER(bound integer) or RAND_INTEGER(seed integer, bound integer) ', '返回0.0(包含)和指定值(不包括)之间的伪随机整数值, 如果指定种子seed，则会等到一个稳定的随机数序列 ', @math, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('LOG', 'null', '计算对数', 'LOG(x numeric) or LOG(base numeric, x numeric)', '未指定base 则以自然数e为底', @math,  -1, -1, -1, 0, 1, 5, now(), now(), 0, null);

set @date=(select id from develop_catalogue where node_name='日期函数' and node_pid = (select id from develop_catalogue where node_name='Flink系统函数'));
INSERT INTO `develop_function`(name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, udf_type, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text)
VALUES ('EXTRACT', 'null', '提取指定单位的时间数值', 'EXTRACT(timeintervalunit FROM temporal)', '提取部分的时间数值,并返回长整形, 比如 EXTRACT(DAY FROM DATE \'2006-06-05\') 返回 5.', @date,  -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('QUARTER', 'null', '计算季节', 'QUARTER(date)', '返回当前时间属性哪个季度 如QUARTER(DATE \'1994-09-27\') 返回 3', @date, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('DATE_FORMAT', 'null', '时间格式化', 'DATE_FORMAT(timestamp, format)', '根据指定format 格式化timestamp 并返回字符串, format 必须和mysql的格式化语法兼容(date_parse), 比如:DATE_FORMAT(ts, \'%Y, %d %M\') results in strings formatted as \'2017, 05 May\'', @date,  -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('TIMESTAMPADD', 'null', '时间加减操作', 'TIMESTAMPADD(unit, interval, timestamp)', '将(有符号)整数interval添加到timestamp. interval的单位由unit参数给出, 它应该是以下值之一：SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, QUARTER, or YEAR. 比如：TIMESTAMPADD(WEEK, 1, \'2003-01-02\') 返回 2003-01-09', @date,  -1, -1, -1, 0, 1, 5, now(), now(), 0, null);


set @char=(select id from develop_catalogue where node_name='字符函数' and node_pid = (select id from develop_catalogue where node_name='Flink系统函数'));
INSERT INTO `develop_function`(name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, udf_type, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text)
VALUES ('CHAR_LENGTH', 'null', '计算字符串长度', 'CHAR_LENGTH(string)', '返回字符串的长度', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('CHARACTER_LENGTH', 'null', '计算字符串长度', 'CHARACTER_LENGTH(string)', '返回字符串的长度', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('UPPER', 'null', '将字符串的字母转换成大写字母', 'UPPER(string)', '将字符串的字母转换成大写字母', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('LOWER', 'null', '将字符串的字母转换成小写字母', 'LOWER(string)', '将字符串的字母转换成小写字母', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('POSITION', 'null', '返回string2中第一次出现string1的位置', 'POSITION(string1 IN string2)', '返回string2中第一次出现string1的位置', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('TRIM', 'null', '删除指定字符', 'TRIM( { BOTH | LEADING | TRAILING } string1 FROM string2)', '从string2 中删除指定位置的String1, 默认是删除前后的空格', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('OVERLAY', 'null', '替换字符串', 'OVERLAY(string1 PLACING string2 FROM integer [ FOR integer2 ])', '用string2替换string1的子字符串', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('SUBSTRING', 'null', '截取字符串', 'SUBSTRING(string FROM integer) or SUBSTRING(string FROM integer FOR integer)', '截取字符串中start位置之后的字符串并返回 截取字符串中start位置之后的长度为length的字符串', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('INITCAP', 'null', '字符串中的单词首字母大写', 'INITCAP(string)', '返回字符串，每个单词的第一个字母大写, 所有其他字母以小写形式显示. 单词由空格分割.', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('CONCAT', 'null', '字符串和字节拼接', 'CONCAT(string1, string2,...)', '将字符串或字节拼接，如：concat(\'foo\', \'bar\') = \'foobar\', 函数可以拼接任意数量的字符串或字节。', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('CONCAT_WS', 'null', '使用指定的分隔符拼接字符串', 'CONCAT_WS(separator, string1, string2,...)', '使用指定的分隔符拼接字符串', @char, -1, -1, -1, 0, 1, 5, now(), now(), 0, null);

set @merge=(select id from develop_catalogue where node_name='聚合函数' and node_pid = (select id from develop_catalogue where node_name='Flink系统函数'));
INSERT INTO `develop_function`(name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, udf_type, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text)
VALUES ('COUNT', 'null', '统计总行数', 'COUNT(*) or COUNT(value [, value]* )', '统计总行数，包括含有NULL值的行, 统计提供非NULL的expr表达式值的行数.', @merge, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('AVG', 'null', '求指定列的平均值', 'AVG(numeric)', '求指定列的平均值.', @merge, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('SUM', 'null', '求指定列的和', 'SUM(numeric)', '求指定列的和.', @merge, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('MAX', 'null', '求指定列的最大值', 'MAX(value)', '求指定列的最大值.', @merge, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('MIN', 'null', '求指定列的最小值', 'MIN(value)', '求指定列的最小值.', @merge, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('STDDEV_POP', 'null', '求指定列数值的标准偏差', 'STDDEV_POP(value)', '求指定列数值的标准偏差.', @merge, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('STDDEV_SAMP', 'null', '求指定列数值的样本标准偏差', 'STDDEV_SAMP(value)', '求指定列数值的样本标准偏差.', @merge, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('VAR_POP', 'null', '求指定列数值的方差', 'VAR_POP(value)', '求指定列数值的方差.', @merge, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('VAR_SAMP', 'null', '求指定列数值的样本方差', 'VAR_POP(value)', '求指定列数值的样本方差.', @merge, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('COLLECT', 'null', '返回包含值的multiset', 'COLLECT(value)', '返回包含值的multiset. null将被忽略.如果仅添加null,则返回一个空multiset.', @merge, -1, -1, -1, 0, 1, 5, now(), now(), 0, null);

set @other=(select id from develop_catalogue where node_name='其它函数' and node_pid = (select id from develop_catalogue where node_name='Flink系统函数'));
INSERT INTO `develop_function`(name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, udf_type, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text)
VALUES ('NULLIF', 'null', '如果值相同着返回null', 'NULLIF(value, value)', '如果值相同着返回null, 比如 NULLIF(5, 5) 返回 NULL; NULLIF(5, 0) 返回 5.', @other, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('COALESCE', 'null', '返回第一非null的值', 'COALESCE(value, value [, value ]* )', '返回第一非null的值, 比如: COALESCE(NULL, 5) 返回 5.', @other, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('CAST', 'null', '类型转换', 'CAST(value AS type)', '将value 转换为指定type', @other, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('GROUP_ID', 'null', '返回一个唯一标识分组键的整数', 'GROUP_ID()', '返回一个唯一标识分组键的整数.', @other,  -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('GROUPING', 'null', '如果表达式在当前行的分组集合中返回1, 否则返回0', 'GROUPING(expression)', '如果表达式在当前行的分组集合中返回1, 否则返回0.', @other, -1, -1, -1, 0, 1, 5, now(), now(), 0, null),
       ('GROUPING_ID', 'null', '返回给定分组表达式的位向量', 'GROUPING_ID(expression [, expression]* )', '返回给定分组表达式的位向量.', @other,  -1, -1, -1, 0, 1, 5, now(), now(), 0, null);

commit;


INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('FlinkSQLFunction', 'FlinkSQLFunction', '4', 'FlinkSQL', 31, 4, 'STRING', '', 1, now(), now(), 0);


DROP TABLE IF EXISTS `stream_metric_support`;
CREATE TABLE `stream_metric_support` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `name` varchar(255) NOT NULL COMMENT '指标中文名称',
  `task_type` tinyint(4) NOT NULL COMMENT '指标支持的任务类型',
  `value` varchar(255) NOT NULL COMMENT '指标key',
  `metric_tag` int(11) NOT NULL COMMENT 'metric匹配模式',
  `component_version` varchar(255) NOT NULL DEFAULT '1.10' COMMENT '组件版本',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(4) DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=256 DEFAULT CHARSET=utf8 COMMENT='任务支持的metric指标';

-- ----------------------------
--  Records of `stream_metric_support_copy`
-- ----------------------------
BEGIN;
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '231', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_numReadPerSecond', '5', '2021-09-26 17:04:01', '1', '各source rps数据输入');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '233', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_byteReadPerSecond', '5', '2021-09-26 17:04:01', '1', '各source bps数据输入');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '235', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_byteWritePerSecond', '5', '2021-09-26 17:04:01', '1', '各sink bps数据输出');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '237', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_numWritePerSecond', '5', '2021-09-26 17:04:01', '1', '各sink rps输出');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '239', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_KafkaConsumer_topic_partition_lag', '5', '2021-09-26 17:04:01', '1', '数据延迟');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '241', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_numReadPerSecond', '6', '2021-09-26 17:04:01', '1', '输入rps');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '243', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_numWritePerSecond', '6', '2021-09-26 17:04:01', '1', '输出rps');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '245', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_byteReadPerSecond', '6', '2021-09-26 17:04:01', '1', '输入bps');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '247', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_byteWritePerSecond', '6', '2021-09-26 17:04:01', '1', '输出bps');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '249', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_numRead', '6', '2021-09-26 17:04:01', '1', '累计输入记录数');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '251', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_numWrite', '6', '2021-09-26 17:04:01', '1', '累计输出记录数');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '253', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_byteRead', '6', '2021-09-26 17:04:01', '1', '累计输入数据量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '255', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_operator_flinkx_byteWrite', '6', '2021-09-26 17:04:01', '1', '累计输出数据量');
  insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '143', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_Status_JVM_CPU_Load', '99', '2021-09-26 17:04:01', '2', 'jobmanager cpu负载');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '145', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_Status_JVM_CPU_Time', '99', '2021-09-26 17:04:01', '2', 'jobmanager cpu使用时间');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '147', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_Status_JVM_CPU_Load', '99', '2021-09-26 17:04:01', '2', 'taskmanager cpu负载');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '149', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_Status_JVM_CPU_Time', '99', '2021-09-26 17:04:01', '2', 'taskmanager cpu使用时间');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '151', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_Status_JVM_Memory_Heap_Max', '99', '2021-09-26 17:04:01', '2', 'bmanager jvm最大堆内存');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '153', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_Status_JVM_Memory_Heap_Max', '99', '2021-09-26 17:04:01', '2', 'taskmanager jvm最大堆内存');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '155', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_Status_JVM_Memory_Heap_Used', '99', '2021-09-26 17:04:01', '2', 'jobmanager jvm堆内存使用量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '157', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_Status_JVM_Memory_Heap_Used', '99', '2021-09-26 17:04:01', '2', 'taskmanager jvm堆内存使用量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '159', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_Status_JVM_Memory_NonHeap_Max', '99', '2021-09-26 17:04:01', '2', 'jobmanager jvm最大非堆内存');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '161', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_Status_JVM_Memory_NonHeap_Max', '99', '2021-09-26 17:04:01', '2', 'taskmanager jvm最大非堆内存');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '163', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_Status_JVM_Memory_NonHeap_Used', '99', '2021-09-26 17:04:01', '2', 'jobmanager jvm非堆内存使用量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '165', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_Status_JVM_Memory_NonHeap_Used', '99', '2021-09-26 17:04:01', '2', 'taskmanager jvm非堆内存使用量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '167', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_Status_JVM_Memory_Direct_TotalCapacity', '99', '2021-09-26 17:04:01', '2', 'jobmanager直接缓冲区总容量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '169', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_Status_JVM_Memory_Direct_TotalCapacity', '99', '2021-09-26 17:04:01', '2', 'taskmanager直接缓冲区总容量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '171', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_Status_JVM_Memory_Direct_MemoryUsed', '99', '2021-09-26 17:04:01', '2', 'jobmanager直接缓冲区内存使用量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '173', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_Status_JVM_Memory_Direct_MemoryUsed', '99', '2021-09-26 17:04:01', '2', 'taskmanager直接缓冲区内存使用量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '175', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_Status_JVM_Threads_Count', '99', '2021-09-26 17:04:01', '2', 'jobmanager 线程数');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '177', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_Status_JVM_Threads_Count', '99', '2021-09-26 17:04:01', '2', 'taskmanager 线程数');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '179', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_buffers_inputQueueLength', '99', '2021-09-26 17:04:01', '1', '排队进入输入缓存区的数量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '181', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_buffers_outputQueueLength', '99', '2021-09-26 17:04:01', '1', '排队进入输出缓存区的数量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '183', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_job_numRestarts', '99', '2021-09-26 17:04:01', '1', '任务重启次数');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '185', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_job_restartingTime', '99', '2021-09-26 17:04:01', '1', '任务重启花费时间');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '187', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_job_lastCheckpointDuration', '99', '2021-09-26 17:04:01', '1', '任务最后一次checkpoint花费时间');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '189', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_job_lastCheckpointSize', '99', '2021-09-26 17:04:01', '1', '任务最后一次checkpoint大小');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '191', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_job_totalNumberOfCheckpoints', '99', '2021-09-26 17:04:01', '1', '任务checkpoint总数量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '193', '1.12', '2021-09-26 17:04:01', 'flink_jobmanager_job_numberOfFailedCheckpoints', '99', '2021-09-26 17:04:01', '1', '任务checkpoint失败数量');
insert into `stream_metric_support` ( `is_deleted`, `id`, `component_version`, `gmt_modified`, `value`, `task_type`, `gmt_create`, `metric_tag`, `name`) values ( '0', '195', '1.12', '2021-09-26 17:04:01', 'flink_taskmanager_job_task_checkpointAlignmentTime', '99', '2021-09-26 17:04:01', '1', 'barrier 对齐花费时间');


alter table tenant add tenant_identity varchar(64) default '' null comment '租户标识';

UPDATE task_template SET value_type = '1.12' WHERE task_type = 2 and type = 0;

COMMIT;
