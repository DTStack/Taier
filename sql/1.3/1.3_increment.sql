-- ----------------------------
-- add script component_model
-- ----------------------------
BEGIN;
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
DELETE FROM task_template WHERE task_type = 12 AND type = 0;
INSERT INTO task_template (task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES
    (12, 0, '', '## 每个worker所占内存，比如512m
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
DELETE FROM task_template WHERE task_type = 13 AND type = 0;
INSERT INTO task_template (task_type, type, value_type, content, gmt_create, gmt_modified, is_deleted) VALUES
 (13, 0, '', '## 每个worker所占内存，比如512m
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
COMMIT;