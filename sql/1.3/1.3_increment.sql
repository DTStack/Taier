BEGIN;
INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '1.12-standalone', '{"1.12-standalone":"flink112-standalone"}', null, 14, 1, 'STRING', 'FLINK', 0, now(), now(), 0);
INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'flink112-standalone', '-120', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);

DELETE FROM dict where dict_code = 'flink_version';

INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('flink_version', '1.12-on-yarn', '112', null, 1, 2, 'INTEGER', '', 0, '2022-05-03 22:13:12', '2022-05-03 22:13:12', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('flink_version', '1.12-standalone', '112_standalone', null, 1, 2, 'INTEGER', '', 0, '2022-09-20 14:57:48', '2022-09-20 14:57:48', 0);

update console_component
set console_component.is_deleted = 1
where console_component.component_type_code in (4, 5, 7);

update dict
set dict_value = replace(dict_value,'1.12', '112')
where dict_code = 'component_model_config' and dict.depend_name = 'YARN';

update dict
set dict_value = replace(dict_value,'2.1', '210')
where dict_code = 'component_model_config' and dict.depend_name = 'YARN';

update dict
set dict_value = replace(dict_value,'2.4', '240')
where dict_code = 'component_model_config' and dict.depend_name = 'YARN';

delete
FROM dict
where dict_name in ('SPARK_THRIFT', 'HIVE_SERVER','OCEAN_BASE') and dict_code = 'component_model';

delete
FROM dict
where depend_name in ('SPARK_THRIFT', 'HIVE_SERVER','OCEAN_BASE') and dict_code = 'component_model_config';


DELETE FROM console_component_config where component_id = -120 and cluster_id = -2;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 0, 'jobmanager.rpc.address', '', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 0, 'jobmanager.rpc.port', '', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 1, 'prometheusHost', '', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 1, 'prometheusPort', '', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 0, 'high-availability', '', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 0, 'high-availability.zookeeper.quorum', '', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 0, 'high-availability.zookeeper.path.root', '', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 0, 'high-availability.storageDir', '', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 0, 'high-availability.cluster-id', '', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin/flink112_lib', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 1, 'chunjunDistDir', '/data/insight_plugin112/chunjunplugin', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 1, 'remoteChunjunDistDir', '/data/insight_plugin112/chunjunplugin', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 1, 'clusterMode', 'standalone', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, '', 0, 'false', 'false', null, 'metrics.reporter.promgateway.deleteOnShutdown', null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, '', 0, 'true', 'true', null, 'metrics.reporter.promgateway.deleteOnShutdown', null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, '', 0, 'false', 'false', null, 'metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, '', 0, 'true', 'true', null, 'metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 0, 'rest.port', '8081', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '112job', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 0, 'state.backend', 'jobmanager', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -120, 0, 'INPUT', 0, 'pluginLoadMode', 'classpath', null, null, null, null, '2022-10-11 14:04:45', '2022-10-11 14:04:45', 0);



-- 任务和集群队列绑定
alter table develop_task add queue_name varchar(64) default  null comment 'yarn队列名称';
alter table develop_task add datasource_id int(11) default  null comment '数据源id';

alter table schedule_task_shade add queue_name varchar(64) default  null comment 'yarn队列名称';
alter table schedule_task_shade add datasource_id int(11) default  null comment '数据源id';

DELETE FROM dict WHERE type in (30,17);

INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('-1', '虚节点', '{"actions": ["SAVE_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "barItem":[ "dependency"],"formField": [], "renderKind": "virtual"}', null, 30, -1, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('0', 'SparkSQL', '{"actions": ["SAVE_TASK", "RUN_TASK", "STOP_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "barItem": ["task", "dependency", "task_params", "env_params"], "formField": ["datasource","queue"], "renderKind": "editor","dataTypeCodes":["45"]}', null, 30, 0, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('1', 'Spark', '{"actions": ["SAVE_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "formField": ["resourceIdList", "mainClass", "exeArgs", "componentVersion"],"barItem":[ "dependency","task_params","env_params"], "renderKind": "spark"}', null, 30, 0, 'STRING', '', 0, '2022-09-03 07:27:25', '2022-09-03 07:27:25', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('2', 'SYNC', '{"actions": ["SAVE_TASK", "RUN_TASK", "STOP_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "barItem": ["task", "dependency", "task_config", "task_params", "env_params"], "formField": ["createModel", "syncModel"], "renderKind": "dataSync", "renderCondition": {"key": "createModel", "value": 0, "renderKind": "editor"}, "actionsCondition": {"key": "createModel", "value": 0, "actions": ["CONVERT_TASK", "SAVE_TASK", "RUN_TASK", "STOP_TASK", "SUBMIT_TASK", "OPERATOR_TASK"]}}', null, 30, 2, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('5', 'FlinkSQL', '{"actions": ["GRAMMAR_TASK", "SAVE_TASK", "OPERATOR_TASK"], "barItem": ["task", "env_params"], "formField": ["componentVersion"], "renderKind": "editor", "actionsCondition": {"key": "createModel", "value": 0, "actions": ["CONVERT_TASK", "FORMAT_TASK", "GRAMMAR_TASK", "SAVE_TASK", "OPERATOR_TASK"]}, "barItemCondition": {"key": "createModel", "value": 0, "barItem": ["task", "flinksql_source", "flinksql_result", "flinksql_dimension", "env_params"]}}', null, 30, 5, 'STRING', '', 0, '2022-09-03 07:25:04', '2022-09-03 07:25:04', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('6', '实时采集', '{"actions": ["SAVE_TASK", "OPERATOR_TASK"], "barItem": ["task", "task_config", "env_params"], "formField": ["createModel", "componentVersion"], "renderKind": "streamCollection", "renderCondition": {"key": "createModel", "value": 0}, "actionsCondition": {"key": "createModel", "value": 0, "actions": ["CONVERT_TASK", "SAVE_TASK", "OPERATOR_TASK"]}}', null, 30, 6, 'STRING', '', 0, '2022-09-03 07:25:04', '2022-09-03 07:25:04', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('7', 'HiveSQL', '{"actions": ["SAVE_TASK", "RUN_TASK", "STOP_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "barItem": ["task", "dependency", "task_params", "env_params"], "formField": ["datasource"], "renderKind": "editor","dataTypeCodes":["27","7","50"]}', null, 30, 0, 'STRING', '', 0, '2022-09-03 07:27:25', '2022-09-03 07:27:25', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('8', 'OceanBaseSQL', '{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":["49"]}', null, 30, 0, 'STRING', '', 0, '2022-09-03 07:27:25', '2022-09-03 07:27:25', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('10', '工作流', '{"actions": ["SAVE_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "barItem": ["task", "dependency"], "formField": [], "renderKind": "workflow"}', null, 30, 0, 'STRING', '', 0, '2022-09-03 07:27:25', '2022-09-03 07:27:25', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('11', 'Flink', '{"actions": ["SAVE_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "formField": ["resourceIdList", "mainClass", "exeArgs", "componentVersion"],"barItem":[ "env_params"], "renderKind": "flink"}', null, 30, 0, 'STRING', '', 0, '2022-09-03 07:27:25', '2022-09-03 07:27:25', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('SYNC', '', '{\"children\":[{\"children\":[{\"name\":\"syncModel\",\"type\":\"number\",\"title\":\"同步模式\",\"noStyle\":true},{\"bind\":{\"field\":\"sourceMap.sourceId\",\"transformer\":\"{{optionCollections.sourceMap_sourceId#find.type}}\"},\"name\":\"type\",\"type\":\"number\",\"title\":\"类型\",\"noStyle\":true},{\"widget\":\"select\",\"name\":\"sourceId\",\"type\":\"number\",\"title\":\"数据源\",\"required\":true,\"props\":{\"method\":\"get\",\"name\":\"sourceMap_sourceId\",\"transformer\":\"sourceIdOnReader\",\"optionsFromRequest\":true,\"placeholder\":\"请选择数据源\",\"url\":\"/taier/api/dataSource/manager/total\"}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"2,4\"}],\"depends\":[\"sourceMap.sourceId\"],\"name\":\"schema\",\"type\":\"number\",\"title\":\"schema\",\"props\":{\"method\":\"post\",\"name\":\"sourcemap_schema\",\"transformer\":\"table\",\"optionsFromRequest\":true,\"placeholder\":\"请选择 schema\",\"params\":{\"sourceId\":\"{{form#sourceMap.sourceId}}\"},\"url\":\"/taier/api/dataSource/addDs/getAllSchemas\",\"required\":[\"sourceId\"]}},{\"widget\":\"SelectWithPreviewer\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"1,2,3,4,7,8,25,27,45,50\"}],\"depends\":[\"sourceMap.sourceId\",\"sourceMap.schema\"],\"name\":\"table\",\"type\":\"string\",\"title\":\"表名\",\"required\":true,\"props\":{\"method\":\"post\",\"name\":\"sourcemap_table\",\"transformer\":\"table\",\"optionsFromRequest\":true,\"placeholder\":\"请选择表名\",\"params\":{\"sourceId\":\"{{form#sourceMap.sourceId}}\",\"schema\":\"{{form#sourceMap.schema}}\",\"isRead\":true,\"isSys\":false},\"url\":\"/taier/api/dataSource/addDs/tablelist\",\"required\":[\"sourceId\"]}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"1,2,3,4,25\"},{\"field\":\"form.sourceMap.syncModel\",\"isNot\":true,\"value\":\"1\"}],\"depends\":[\"sourceMap.table\"],\"name\":\"increColumn\",\"type\":\"string\",\"title\":\"增量标识字段\",\"required\":true,\"props\":{\"method\":\"post\",\"name\":\"sourcemap_increColumn\",\"transformer\":\"incrementColumn\",\"optionsFromRequest\":true,\"placeholder\":\"请选择增量标识字段\",\"params\":{\"sourceId\":\"{{form#sourceMap.sourceId}}\",\"schema\":\"{{form#sourceMap.schema}}\",\"tableName\":\"{{form#sourceMap.table}}\"},\"url\":\"/taier/api/task/getIncreColumn\",\"required\":[\"sourceId\",\"tableName\"]}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"8\"}],\"name\":\"encoding\",\"type\":\"string\",\"title\":\"编码\",\"initialValue\":\"utf-8\",\"required\":true,\"props\":{\"options\":[{\"label\":\"utf-8\",\"value\":\"utf-8\"},{\"label\":\"gdb\",\"value\":\"gdb\"}],\"placeholder\":\"请选择编码\"}},{\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"8\"}],\"name\":\"startRowkey\",\"type\":\"string\",\"title\":\"开始行健\",\"props\":{\"placeholder\":\"请输入开始行健\"}},{\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"8\"}],\"name\":\"endRowkey\",\"type\":\"string\",\"title\":\"结束行健\",\"props\":{\"placeholder\":\"请输入结束行健\"}},{\"widget\":\"radio\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"8\"}],\"name\":\"isBinaryRowkey\",\"type\":\"string\",\"title\":\"行健二进制转换\",\"initialValue\":\"0\",\"props\":{\"options\":[{\"label\":\"FALSE\",\"value\":\"0\"},{\"label\":\"TRUE\",\"value\":\"1\"}]}},{\"widget\":\"inputNumber\",\"hidden\":[{\"field\":\"form.sourceMap.t ype\",\"isNot\":true,\"value\":\"8\"}],\"name\":\"scanCacheSize\",\"type\":\"string\",\"title\":\"每次RPC请求获取行数\",\"props\":{\"min\":0,\"placeholder\":\"请输入大小, 默认为256\",\"suffix\":\"行\"}},{\"widget\":\"inputNumber\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"8\"}],\"name\":\"scanBatchSize\",\"type\":\"string\",\"title\":\"每次RPC请求获取列数\",\"props\":{\"min\":0,\"placeholder\":\"请输入大小, 默认为100\",\"suffix\":\"列\"}},{\"widget\":\"textarea\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"1,2,3,4,25\"}],\"name\":\"where\",\"rules\":[{\"max\":1000,\"message\":\"过滤语句不可超过1000个字符!\"}],\"type\":\"string\",\"title\":\"数据过滤\",\"props\":{\"autoSize\":{\"minRows\":2,\"maxRows\":6},\"placeholder\":\"请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步\"}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"1,2,3,4,25\"}],\"depends\":[\"sourceMap.table\"],\"name\":\"split\",\"type\":\"string\",\"title\":\"切分键\",\"props\":{\"method\":\"post\",\"name\":\"sourcemap_split\",\"transformer\":\"split\",\"optionsFromRequest\":true,\"placeholder\":\"请选择切分键\",\"params\":{\"sourceId\":\"{{form#sourceMap.sourceId}}\",\"schema\":\"{{form#sourceMap.schema}}\",\"tableName\":\"{{form#sourceMap.table#toArray}}\"},\"url\":\"/taier/api/dataSource/addDs/columnForSyncopate\",\"required\":[\"sourceId\",\"tableName\"]}},{\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"6\"}],\"name\":\"path\",\"rules\":[{\"max\":200,\"message\":\"路径不得超过200个字符！\"}],\"type\":\"string\",\"title\":\"路径\",\"required\":true,\"props\":{\"placeholder\":\"例如: /rdos/batch\"}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"6\"}],\"name\":\"fileType\",\"type\":\"string\",\"title\":\"文件类型\",\"initialValue\":\"text\",\"required\":true,\"props\":{\"options\":[{\"label\":\"orc\",\"value\":\"orc\"},{\"label\":\"text\",\"value\":\"text\"},{\"label\":\"parquet\",\"value\":\"parquet\"}],\"placeholder\":\"请选择文件类型\"}},{\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"6\"},{\"field\":\"form.sourceMap.fileType\",\"isNot\":true,\"value\":\"text\"}],\"name\":\"fieldDelimiter\",\"type\":\"string\",\"title\":\"列分隔符\",\"props\":{\"placeholder\":\"若不填写，则默认为\\\\\\\\001\"}},{\"widget\":\"autoComplete\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"7,27,45,50\"}],\"depends\":[\"sourceMap.table\"],\"name\":\"partition\",\"type\":\"string\",\"title\":\"分区\",\"props\":{\"method\":\"post\",\"name\":\"sourcemap_partition\",\"transformer\":\"table\",\"optionsFromRequest\":true,\"placeholder\":\"请填写分区信息\",\"params\":{\"sourceId\":\"{{form#sourceMap.sourceId}}\",\"tableName\":\"{{form#sourceMap.table}}\"},\"url\":\"/taier/api/dataSource/addDs/getHivePartitions\",\"required\":[\"sourceId\",\"tableName\"]}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"11,33,46\"}],\"depends\":[\"sourceMap.sourceId\"],\"name\":\"index\",\"type\":\"string\",\"title\":\"index\",\"required\":true,\"props\":{\"method\":\"post\",\"name\":\"sourcemap_schema\",\"transformer\":\"table\",\"optionsFromRequest\":true,\"placeholder\":\"请选择index\",\"params\":{\"sourceId\":\"{{form#sourceMap.sourceId}}\"},\"url\":\"/taier/api/dataSource/addDs/getAllSchemas\",\"required\":[\"sourceId\"]}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"11,33\"}],\"depends\":[\"sourceMap.index\"],\"name\":\"indexType\",\"type\":\"string\",\"title\":\"type\",\"required\":true,\"props\":{\"method\":\"post\",\"name\":\"sourcemap_table\",\"transformer\":\"table\",\"optionsFromRequest\":true,\"placeholder\":\"请选择indexType！\",\"params\":{\"sourceId\":\"{{form#sourceMap.sourceId}}\",\"schema\":\"{{form#sourceMap.schema}}\",\"isRead\":true,\"isSys\":false},\"url\":\"/taier/api/dataSource/addDs/tablelist\",\"required\":[\"sourceId\",\"schema\"]}},{\"widget\":\"textarea\",\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"11,33,46\"}],\"name\":\"query\",\"rules\":[{\"max\":1024,\"message\":\"仅支持1-1024个任意字符\"}],\"type\":\"string\",\"title\":\"query\",\"props\":{\"autoSize\":{\"minRows\":2,\"maxRows\":6},\"placeholder\":\"\\\"match_all\\\":{}\\\"\"}},{\"widget\":\"textarea\",\"hidden\":[{\"field\":\"form.sourceMap.sourceId\",\"value\":\"undefined\"}],\"name\":\"extralConfig\",\"validator\":\"json\",\"type\":\"string\",\"title\":\"高级配置\",\"props\":{\"autoSize\":{\"minRows\":2,\"maxRows\":6},\"placeholder\":\"以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize\"}},{\"hidden\":true,\"name\":\"column\",\"type\":\"string\",\"title\":\"列\"}],\"name\":\"sourceMap\",\"type\":\"object\",\"title\":\"数据来源\"},{\"children\":[{\"bind\":{\"field\":\"targetMap.sourceId\",\"transformer\":\"{{optionCollections.targetmap_sourceId#find.type}}\"},\"name\":\"type\",\"type\":\"number\",\"title\":\"类型\",\"noStyle\":true},{\"widget\":\"select\",\"name\":\"sourceId\",\"type\":\"number\",\"title\":\"数据源\",\"required\":true,\"props\":{\"method\":\"get\",\"name\":\"targetmap_sourceId\",\"transformer\":\"sourceIdOnWriter\",\"optionsFromRequest\":true,\"placeholder\":\"请选择数据源\",\"url\":\"/taier/api/dataSource/manager/total\"}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"2,4,64\"}],\"depends\":[\"targetMap.sourceId\"],\"name\":\"schema\",\"type\":\"number\",\"title\":\"schema\",\"props\":{\"method\":\"post\",\"name\":\"targetmap_schema\",\"transformer\":\"table\",\"optionsFromRequest\":true,\"placeholder\":\"请选择 schema\",\"params\":{\"sourceId\":\"{{form#targetMap.sourceId}}\"},\"url\":\"/taier/api/dataSource/addDs/getAllSchemas\",\"required\":[\"sourceId\"]}},{\"widget\":\"SelectWithCreate\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"1,2,3,4,7,8,25,27,45,50,64\"}],\"depends\":[\"targetMap.sourceId\",\"targetMap.schema\"],\"name\":\"table\",\"type\":\"string\",\"title\":\"表名\",\"required\":true,\"props\":{\"method\":\"post\",\"name\":\"targetmap_table\",\"transformer\":\"table\",\"optionsFromRequest\":true,\"placeholder\":\"请选择表名\",\"params\":{\"sourceId\":\"{{form#targetMap.sourceId}}\",\"schema\":\"{{form#targetMap.schema}}\",\"isRead\":true,\"isSys\":false},\"url\":\"/taier/api/dataSource/addDs/tablelist\",\"required\":[\"sourceId\"]}},{\"widget\":\"autoComplete\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"7,27,45,50\"}],\"depends\":[\"targetMap.table\"],\"name\":\"partition\",\"type\":\"string\",\"title\":\"分区\",\"props\":{\"method\":\"post\",\"name\":\"targetmap_partition\",\"transformer\":\"table\",\"optionsFromRequest\":true,\"placeholder\":\"请填写分区信息\",\"params\":{\"sourceId\":\"{{form#targetMap.sourceId}}\",\"tableName\":\"{{form#targetMap.table}}\"},\"url\":\"/taier/api/dataSource/addDs/getHivePartitions\",\"required\":[\"sourceId\",\"tableName\"]}},{\"widget\":\"textarea\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"1,2,3,4,25,64\"}],\"depends\":[\"targetMap.type\"],\"name\":\"preSql\",\"type\":\"string\",\"title\":\"导入前准备语句\",\"props\":{\"autoSize\":{\"minRows\":2,\"maxRows\":6},\"placeholder\":\"请输入导入数据前执行的 SQL 脚本\"}},{\"widget\":\"textarea\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"1,2,3,4,25,64\"}],\"depends\":[\"targetMap.type\"],\"name\":\"postSql\",\"type\":\"string\",\"title\":\"导入后准备语句\",\"props\":{\"autoSize\":{\"minRows\":2,\"maxRows\":6},\"placeholder\":\"请输入导入数据后执行的 SQL 脚本\"}},{\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"6\"}],\"name\":\"path\",\"rules\":[{\"max\":200,\"message\":\"路径不得超过200个字符！\"}],\"type\":\"string\",\"title\":\"路径\",\"required\":true,\"props\":{\"placeholder\":\"例如: /app/batch\"}},{\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"6\"}],\"name\":\"fileName\",\"type\":\"string\",\"title\":\"文件名\",\"required\":true,\"props\":{\"placeholder\":\"请输入文件名\"}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"6\"}],\"name\":\"fileType\",\"type\":\"string\",\"title\":\"文件类型\",\"initialValue\":\"orc\",\"required\":true,\"props\":{\"options\":[{\"label\":\"orc\",\"value\":\"orc\"},{\"label\":\"text\",\"value\":\"text\"},{\"label\":\"parquet\",\"value\":\"parquet\"}],\"placeholder\":\"请选择文件类型\"}},{\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"6\"}],\"name\":\"fieldDelimiter\",\"type\":\"string\",\"title\":\"列分隔符\",\"initialValue\":\",\",\"props\":{\"placeholder\":\"例如: 目标为 hive 则分隔符为\\\\\\\\001\"}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"6,8\"}],\"name\":\"encoding\",\"type\":\"string\",\"title\":\"编码\",\"initialValue\":\"utf-8\",\"required\":true,\"props\":{\"options\":[{\"label\":\"utf-8\",\"value\":\"utf-8\"},{\"label\":\"gdb\",\"value\":\"gdb\"}],\"placeholder\":\"请选择编码\"}},{\"widget\":\"radio\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"2,4,6,7,25,27,45,50\"}],\"depends\":[\"targetMap.sourceId\"],\"name\":\"writeMode\",\"type\":\"string\",\"title\":\"写入模式\",\"required\":true,\"props\":{\"options\":[{\"label\":\"覆盖（Insert Overwrite）\",\"value\":\"replace\"},{\"label\":\"追加（Insert Into）\",\"value\":\"insert\"}]}},{\"widget\":\"radio\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"8\"}],\"name\":\"nullMode\",\"type\":\"string\",\"title\":\"读取为空时的处理方式\",\"initialValue\":\"skip\",\"props\":{\"options\":[{\"label\":\"SKIP\",\"value\":\"skip\"},{\"label\":\"EMPTY\",\"value\":\"empty\"}]}},{\"widget\":\"inputNumber\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"8\"}],\"name\":\"writeBufferSize\",\"type\":\"string\",\"title\":\"写入缓存大小\",\"props\":{\"placeholder\":\"请输入缓存大小\",\"suffix\":\"KB\"}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"11,33,46\"}],\"depends\":[\"targetMap.sourceId\"],\"name\":\"index\",\"type\":\"string\",\"title\":\"index\",\"required\":true,\"props\":{\"method\":\"post\",\"name\":\"targetmap_schema\",\"transformer\":\"table\",\"optionsFromRequest\":true,\"placeholder\":\"请选择index\",\"params\":{\"sourceId\":\"{{form#targetMap.sourceId}}\"},\"url\":\"/taier/api/dataSource/addDs/getAllSchemas\",\"required\":[\"sourceId\"]}},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"11,33\"}],\"depends\":[\"targetMap.index\"],\"name\":\"indexType\",\"type\":\"string\",\"title\":\"type\",\"required\":true,\"props\":{\"method\":\"post\",\"name\":\"targetmap_table\",\"transformer\":\"table\",\"optionsFromRequest\":true,\"placeholder\":\"请选择indexType！\",\"params\":{\"sourceId\":\"{{form#targetMap.sourceId}}\",\"schema\":\"{{form#targetMap.schema}}\",\"isRead\":true,\"isSys\":false},\"url\":\"/taier/api/dataSource/addDs/tablelist\",\"required\":[\"sourceId\",\"schema\"]}},{\"widget\":\"inputNumber\",\"hidden\":[{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"11,33,46\"}],\"name\":\"bulkAction\",\"type\":\"number\",\"title\":\"bulkAction\",\"initialValue\":100,\"required\":true,\"props\":{\"min\":1,\"max\":200000,\"precision\":0,\"placeholder\":\"请输入 bulkAction\"}},{\"widget\":\"textarea\",\"hidden\":[{\"field\":\"form.targetMap.sourceId\",\"value\":\"undefined\"}],\"name\":\"extralConfig\",\"validator\":\"json\",\"type\":\"string\",\"title\":\"高级配置\",\"props\":{\"autoSize\":{\"minRows\":2,\"maxRows\":6},\"placeholder\":\"以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize\"}},{\"hidden\":true,\"name\":\"column\",\"type\":\"string\",\"title\":\"列\"}],\"name\":\"targetMap\",\"type\":\"object\",\"title\":\"选择目标\"},{\"children\":[{\"widget\":\"KeyMap\",\"type\":\"any\"}],\"name\":\"mapping\",\"type\":\"object\",\"title\":\"字段映射\"},{\"children\":[{\"widget\":\"autoComplete\",\"name\":\"speed\",\"type\":\"string\",\"title\":\"作业速率上限\",\"initialValue\":\"不限制传输速率\",\"required\":true,\"props\":{\"options\":[{\"value\":\"不限制传输速率\"},{\"value\":\"1\"},{\"value\":\"2\"},{\"value\":\"3\"},{\"value\":\"4\"},{\"value\":\"5\"},{\"value\":\"6\"},{\"value\":\"7\"},{\"value\":\"8\"},{\"value\":\"9\"},{\"value\":\"10\"}],\"placeholder\":\"请选择作业速率上限\",\"suffix\":\"MB/s\"}},{\"widget\":\"autoComplete\",\"name\":\"channel\",\"type\":\"string\",\"title\":\"作业并发数\",\"initialValue\":\"1\",\"required\":true,\"props\":{\"options\":[{\"value\":\"1\"},{\"value\":\"2\"},{\"value\":\"3\"},{\"value\":\"4\"},{\"value\":\"5\"}],\"placeholder\":\"请选择作业并发数\"}},{\"hidden\":[{\"field\":\"form.sourceMap.type\",\"isNot\":true,\"value\":\"1,2,3,4,8,19,22,24,25,28,29,31,32,35,36,40,53,54,61,71,73\"},{\"field\":\"form.targetMap.type\",\"isNot\":true,\"value\":\"1,2,3,4,7,8,10,19,22,24,25,27,28,29,31,32,35,36,40,53,54,61,71,73\"}],\"name\":\"isRestore\",\"type\":\"boolean\",\"title\":\"断点续传\"},{\"widget\":\"select\",\"hidden\":[{\"field\":\"form.settingMap.isRestore\",\"value\":\"false,undefined\"}],\"name\":\"restoreColumnName\",\"type\":\"string\",\"title\":\"标识字段\",\"required\":true,\"props\":{\"method\":\"post\",\"name\":\"settingmap_restore\",\"transformer\":\"restore\",\"optionsFromRequest\":true,\"placeholder\":\"请选择标识字段\",\"params\":{\"sourceId\":\"{{form#sourceMap.sourceId}}\",\"schema\":\"{{form#sourceMap.schema}}\",\"tableName\":\"{{form#sourceMap.table}}\"},\"url\":\"/taier/api/task/getIncreColumn\",\"required\":[\"sourceId\",\"tableName\"]}}],\"name\":\"settingMap\",\"type\":\"object\",\"title\":\"通道控制\"}],\"type\":\"object\"}', NULL, 17, 1, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
-- ----------------------------
-- add script component_model
-- ----------------------------
DELETE FROM dict WHERE dict_code = 'component_model' AND dict_name = 'SCRIPT';
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('component_model', 'SCRIPT', '{"owner": "COMPUTE", "dependsOn": ["RESOURCE", "STORAGE"], "allowKerberos": "true", "allowCoexistence": false, "uploadConfigType": "0", "versionDictionary": ""}', null, 12, 0, 'STRING', '', 0, now(), now(), 0);

DELETE FROM dict WHERE dict_code = 'typename_mapping' AND dict_name = 'yarn2-hdfs2-script';
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('typename_mapping', 'yarn2-hdfs2-script', '-100', null, 6, 0, 'LONG', '', 0, now(), now(), 0);

DELETE FROM dict WHERE dict_code = 'typename_mapping' AND dict_name = 'yarn3-hdfs3-script';
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('typename_mapping', 'yarn3-hdfs3-script', '-100', null, 6, 0, 'LONG', '', 0, now(), now(), 0);

DELETE FROM `console_component_config` WHERE `cluster_id` = -2 AND `component_id` = -100 AND `component_type_code` = 8;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'script.java.opts', '-Dfile.encoding=UTF-8', NULL, NULL, NULL, NULL, now(), now(), 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'script.am.memory', '512m', NULL, NULL, NULL, NULL, now(), now(), 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'script.am.cores', '1', NULL, NULL, NULL, NULL, now(), now(), 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'script.worker.memory', '512m', NULL, NULL, NULL, NULL, now(), now(), 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'script.worker.cores', '1', NULL, NULL, NULL, NULL, now(), now(), 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'script.worker.num', '1', NULL, NULL, NULL, NULL, now(), now(), 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'container.staging.dir', '/insight/script/staging', NULL, NULL, NULL, NULL, now(), now(), 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'script.container.heartbeat.interval', '10000', NULL, NULL, NULL, NULL, now(), now(), 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'script.container.heartbeat.timeout', '120000', NULL, NULL, NULL, NULL, now(), now(), 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'script.python2.path', '/data/miniconda2/bin/python2', NULL, NULL, NULL, NULL, now(), now(), 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -100, 8, 'INPUT', 1, 'script.python3.path', '/data/miniconda3/bin/python3', NULL, NULL, NULL, NULL, now(), now(), 0);

-- task template
-- python
DELETE FROM task_param_template WHERE task_type = 12;
INSERT INTO task_param_template (task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES
    (12, 'PYTHON', '', '## 每个worker所占内存，比如512m
# script.worker.memory=512m

## 每个worker所占的cpu核的数量
# script.worker.cores=1

## worker数量
# script.worker.num=1

## 是否独占机器节点
# script.worker.exclusive=false

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## 指定work运行节点，需要注意不要写ip应填写对应的hostname
# script.worker.nodes=

## 指定work运行机架
# script.worker.racks=

## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
logLevel=INFO',now(),now(), 0);

-- shell
DELETE FROM task_param_template WHERE task_type = 13;
INSERT INTO task_param_template (task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES
    (13, 'SHELL', '', '## 每个worker所占内存，比如512m
# script.worker.memory=512m

## 每个worker所占的cpu核的数量
# script.worker.cores=1

## worker数量
# script.worker.num=1

## 是否独占机器节点
# script.worker.exclusive=false

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## 指定work运行节点，需要注意不要写ip应填写对应的hostname
# script.worker.nodes=

## 指定work运行机架
# script.worker.racks=

## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
logLevel=INFO',now(),now(), 0);

-- change old name
update dict set dict_value = REPLACE(dict_value, '-dtscript', '-script') where `type` = 14 and depend_name = 'YARN' and dict_value like '%dtscript%';
update dict set dict_value = REPLACE(dict_value, 'DT_SCRIPT', 'SCRIPT') where `type` = 14 and depend_name = 'YARN' and dict_value like '%DT_SCRIPT%';

delete from dict where `type` = 30 and dict_code in ('12', '13');
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('12', 'Python', '{"actions": ["SAVE_TASK", "RUN_TASK", "STOP_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "barItem": ["task", "dependency", "task_params", "env_params"], "formField": ["pythonVersion"], "renderKind": "editor"}', null, 30, 0, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('13', 'Shell', '{"actions": ["SAVE_TASK", "RUN_TASK", "STOP_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "barItem": ["task", "dependency", "task_params", "env_params"], "formField": [], "renderKind": "editor"}', null, 30, 0, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);

delete from dict where `type` = 25 and dict_desc = '8';
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'script.java.opts', 'script container jvm 扩展参数', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'script.am.memory', 'am container 使用的内存量', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'script.am.cores', 'am container 使用的 cpu 核数', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'script.worker.memory', 'work container 使用的内存量', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'script.worker.cores', 'work container 使用的 cpu 核数', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'script.worker.num', 'work container 实例数量', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'container.staging.dir', '任务临时文件路径', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'script.container.heartbeat.interval', 'am 和 work 之间的心跳间隔，单位毫秒', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'script.container.heartbeat.timeout', 'am 和 work 之间的心跳超时时间，单位毫秒', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'script.python2.path', 'python2.x 二进制可执行文件地址', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'script.python3.path', 'python3.x 二进制可执行文件地址', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);

truncate table datasource_classify;
INSERT INTO `datasource_classify` VALUES (1, 'total', 100, '全部', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:42', 0, 0);
INSERT INTO `datasource_classify` VALUES (2, 'mostUse', 90, '常用', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO `datasource_classify` VALUES (3, 'relational', 80, '关系型', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO `datasource_classify` VALUES (4, 'bigData', 70, '大数据存储', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO `datasource_classify` VALUES (5, 'mpp', 60, 'MPP', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO `datasource_classify` VALUES (6, 'semiStruct', 50, '半结构化', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO `datasource_classify` VALUES (7, 'analytic', 40, '分析型', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO `datasource_classify` VALUES (8, 'NoSQL', 30, 'NoSQL', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO `datasource_classify` VALUES (0, 'actualTime', 20, '实时', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO `datasource_classify` VALUES (10, 'api', 0, '接口', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO `datasource_classify` VALUES (11, 'sequential', 10, '时序', 0, '2021-06-09 17:19:27', '2021-06-09 17:19:27', 0, 0);



INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, sorted, invisible) VALUES ('OceanBase', 5, 1.0, 'OceanBase.png', 0, '2021-08-05 10:22:10', '2021-08-17 11:53:29', 0, 0, 1200, 0);

INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, type_version, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:oceanbase://host:port/dbName', null, '/jdbc:oceanbase:\\/\\/(.)+/', 0, '2021-08-05 09:35:57', '2021-08-05 16:07:17', 0, 0, 'OceanBase', '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, type_version, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 0, '2021-08-05 09:35:57', '2021-08-05 10:08:08', 0, 0, 'OceanBase', '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, type_version, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 0, '2021-08-05 09:35:57', '2021-08-05 10:08:12', 0, 0, 'OceanBase', '');


-- ----------------------------
-- 增加 clickhouse
-- ----------------------------
DELETE FROM dict WHERE type = 30 AND dict_code = '14' ;
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('14', 'ClickHouseSQL', '{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":[25]}', null, 30, 0, 'STRING', '', 0, now(), now(), 0);

DELETE FROM dict WHERE type = 30 AND dict_code = '15' ;
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('15', 'DorisSQL','{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":[64]}', null, 30, 0, 'STRING', '', 0, now(), now(), 0);



-- ----------------------------
-- 增加 doris
-- ----------------------------

-- 修改 doris 数据源模板
UPDATE datasource_form_field SET `name` = 'url', `label` = 'url' ,`place_hold` = 'http://localhost:8030', `valid_info` = '{"regex":{"message":"URL格式不符合规则!"}}',`tooltip` = '',`regex`='' WHERE `type_version` = 'Doris-0.14.x' AND name = 'jdbcUrl';
INSERT INTO `datasource_form_field` (`name`, `label`, `widget`, `required`, `invisible`, `default_value`, `place_hold`, `request_api`, `is_link`, `valid_info`, `tooltip`, `style`, `regex`, `type_version`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`, `options`) VALUES
    ('schema', 'schema', 'Input', '1', '0', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, 'Doris-0.14.x', '0', now(), now(), '0', '0', '');


INSERT INTO task_param_template (task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES ( 1, 'SPARK', '2.1', '## Driver程序使用的CPU核数,默认为1
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
# spark.yarn.executor.memoryOverhead',now(),now(), 0);


INSERT INTO `task_param_template`(task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES ( 1, 'SPARK', '2.1', '## Driver程序使用的CPU核数,默认为1\n# driver.cores=1\n\n## Driver程序使用内存大小,默认512m\n# driver.memory=512m\n\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\n# driver.maxResultSize=1g\n\n## 启动的executor的数量，默认为1\nexecutor.instances=1\n\n## 每个executor使用的CPU核数，默认为1\nexecutor.cores=1\n\n## 每个executor内存大小,默认512m\nexecutor.memory=512m\n\n## 任务优先级, 值越小，优先级越高，范围:1-1000\njob.priority=10\n\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\n# logLevel = INFO\n\n## spark中所有网络交互的最大超时时间\n# spark.network.timeout=120s\n\n## executor的OffHeap内存，和spark.executor.memory配置使用\n# spark.yarn.executor.memoryOverhead', '2021-11-18 10:36:13', '2021-11-18 10:36:13', 0);
INSERT INTO `task_param_template`(task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES ( 5, 'FlinkSQL', '1.12', '## 资源相关\nparallelism.default=1\ntaskmanager.numberOfTaskSlots=1\njobmanager.memory.process.size=1g\ntaskmanager.memory.process.size=2g\n\n## 时间相关\n## 设置Flink时间选项，有ProcessingTime,EventTime,IngestionTime可选\n## 非脚本模式会根据Kafka自动设置。脚本模式默认为ProcessingTime\n# pipeline.time-characteristic=EventTime\n\n## Checkpoint相关\n## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成\nexecution.checkpointing.interval=5min\n## 状态恢复语义,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE\n# execution.checkpointing.mode=EXACTLY_ONCE\n##任务取消后保留hdfs上的checkpoint文件\nexecution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION\n\n# Flink SQL独有，状态过期时间\ntable.exec.state.ttl=1d\n\nlog.level=INFO\n\n## 使用Iceberg和Hive维表开启\n# table.dynamic-table-options.enabled=true\n\n## Kerberos相关\n# security.kerberos.login.contexts=Client,KafkaClient\n\n\n## 高阶参数\n## 窗口提前触发时间\n# table.exec.emit.early-fire.enabled=true\n# table.exec.emit.early-fire.delay=1s\n\n## 当一个源在超时时间内没有收到任何元素时，它将被标记为临时空闲\n# table.exec.source.idle-timeout=10ms\n\n## 是否开启minibatch\n## 可以减少状态开销。这可能会增加一些延迟，因为它会缓冲一些记录而不是立即处理它们。这是吞吐量和延迟之间的权衡\n# table.exec.mini-batch.enabled=true\n## 状态缓存时间\n# table.exec.mini-batch.allow-latency=5s\n## 状态最大缓存条数\n# table.exec.mini-batch.size=5000\n\n## 是否开启Local-Global 聚合。前提需要开启minibatch\n## 聚合是为解决数据倾斜问题提出的，类似于 MapReduce 中的 Combine + Reduce 模式\n# table.optimizer.agg-phase-strategy=TWO_PHASE\n\n## 是否开启拆分 distinct 聚合\n## Local-Global 可以解决数据倾斜，但是在处理 distinct 聚合时，其性能并不令人满意。\n## 如：SELECT day, COUNT(DISTINCT user_id) FROM T GROUP BY day 如果 distinct key （即 user_id）的值分布稀疏，建议开启\n# table.optimizer.distinct-agg.split.enabled=true\n\n\n## Flink算子chaining开关。默认为true。排查性能问题时会暂时设置成false，但降低性能。\n# pipeline.operator-chaining=true', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO `task_param_template`(task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES ( 6, '实时采集', '1.12', '## 资源相关\nparallelism.default=1\ntaskmanager.numberOfTaskSlots=1\njobmanager.memory.process.size=1g\ntaskmanager.memory.process.size=2g\n\n## 时间相关\n## 设置Flink时间选项，有ProcessingTime,EventTime,IngestionTime可选\n## 非脚本模式会根据Kafka自动设置。脚本模式默认为ProcessingTime\n# pipeline.time-characteristic=EventTime\n\n## Checkpoint相关\n## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成\nexecution.checkpointing.interval=5min\n## 状态恢复语义,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE\n# execution.checkpointing.mode=EXACTLY_ONCE\n##任务取消后保留hdfs上的checkpoint文件\nexecution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION\n\n# Flink SQL独有，状态过期时间\ntable.exec.state.ttl=1d\n\nlog.level=INFO\n\n## 使用Iceberg和Hive维表开启\n# table.dynamic-table-options.enabled=true\n\n## Kerberos相关\n# security.kerberos.login.contexts=Client,KafkaClient\n\n\n## 高阶参数\n## 窗口提前触发时间\n# table.exec.emit.early-fire.enabled=true\n# table.exec.emit.early-fire.delay=1s\n\n## 当一个源在超时时间内没有收到任何元素时，它将被标记为临时空闲\n# table.exec.source.idle-timeout=10ms\n\n## 是否开启minibatch\n## 可以减少状态开销。这可能会增加一些延迟，因为它会缓冲一些记录而不是立即处理它们。这是吞吐量和延迟之间的权衡\n# table.exec.mini-batch.enabled=true\n## 状态缓存时间\n# table.exec.mini-batch.allow-latency=5s\n## 状态最大缓存条数\n# table.exec.mini-batch.size=5000\n\n## 是否开启Local-Global 聚合。前提需要开启minibatch\n## 聚合是为解决数据倾斜问题提出的，类似于 MapReduce 中的 Combine + Reduce 模式\n# table.optimizer.agg-phase-strategy=TWO_PHASE\n\n## 是否开启拆分 distinct 聚合\n## Local-Global 可以解决数据倾斜，但是在处理 distinct 聚合时，其性能并不令人满意。\n## 如：SELECT day, COUNT(DISTINCT user_id) FROM T GROUP BY day 如果 distinct key （即 user_id）的值分布稀疏，建议开启\n# table.optimizer.distinct-agg.split.enabled=true\n\n\n## Flink算子chaining开关。默认为true。排查性能问题时会暂时设置成false，但降低性能。\n# pipeline.operator-chaining=true', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO `task_param_template` (task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES (2, 'SYNC', '1.12', '## 任务运行方式：
## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步
## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认per_job
## standalone：多个任务共用一个flink standalone
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
## job.priority=10', '2021-11-18 10:37:24', '2021-11-18 10:37:24', 0);

COMMIT;