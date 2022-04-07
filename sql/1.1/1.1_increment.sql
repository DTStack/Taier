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
ADD COLUMN `side_str` longtext COMMENT '维表' AFTER `target_str`;


INSERT INTO task_param_template (task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES (5, 'FlinkSQL', '1.12', '## 资源相关
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
# pipeline.operator-chaining=true', '2022-04-06 18:37:24', '2022-04-06 18:37:24', 0);
