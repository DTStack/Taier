-- 插入flink-on-standalone组件的字典项
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ( 'typename_mapping', 'flink-on-standalone', '-113', NULL, 6, 0, 'LONG', '', 0, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'jobmanager.rpc.address', '', null, null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'jobmanager.rpc.port', '', null, null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'rest.port', '', null, null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 0, 'prometheusHost', '', null, null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 0, 'prometheusPort', '', null, null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'metrics.reporter.promgateway.class', '', null, null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 0, 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', '', null, null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', '', null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', '', null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '', null, null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'metrics.reporter.promgateway.port', '', null, null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'flinkJarPath', '/data/insight_plugin/flink110_lib', '', null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'flinkPluginRoot', '/data/insight_plugin', '', null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'remotePluginRootDir', '/data/insight_plugin', '', null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'pluginLoadMode', 'classpath', '', null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 1, 'clusterMode', 'standalone', '', null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -113, 20, 'INPUT', 0, 'state.backend', 'jobmanager', '', null, null, null, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES ( -2, -113, 20, '', '0', 'false', 'false', NULL, 'metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL, '2021-05-19 11:48:25', '2021-05-19 11:48:25', '0');
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES ( -2, -113, 20, '', '0', 'true', 'true', NULL, 'metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL, '2021-05-19 11:48:25', '2021-05-19 11:48:25', '0');
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES ( -2, -113, 20, '', '0', 'false', 'false', NULL, 'metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL, '2021-05-19 11:48:25', '2021-05-19 11:48:25', '0');
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES ( -2, -113, 20, '', '0', 'true', 'true', NULL, 'metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL, '2021-05-19 11:48:25', '2021-05-19 11:48:25', '0');
