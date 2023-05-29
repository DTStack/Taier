BEGIN;

-- supported ftp source and target on the sync
UPDATE dict
SET dict_value = '{"children":[{"children":[{"name":"syncModel","type":"number","title":"同步模式","noStyle":true},{"bind":{"field":"sourceMap.sourceId","transformer":"{{optionCollections.sourceMap_sourceId#find.type}}"},"name":"type","type":"number","title":"类型","noStyle":true},{"widget":"select","name":"sourceId","type":"number","title":"数据源","required":true,"props":{"method":"get","name":"sourceMap_sourceId","transformer":"sourceIdOnReader","optionsFromRequest":true,"placeholder":"请选择数据源","url":"/taier/api/dataSource/manager/total"}},{"widget":"select","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"2,4"}],"depends":["sourceMap.sourceId"],"name":"schema","type":"number","title":"schema","props":{"method":"post","name":"sourcemap_schema","transformer":"table","optionsFromRequest":true,"placeholder":"请选择 schema","params":{"sourceId":"{{form#sourceMap.sourceId}}"},"url":"/taier/api/dataSource/addDs/getAllSchemas","required":["sourceId"]}},{"widget":"SelectWithPreviewer","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"1,2,3,4,7,8,25,27,45,50"}],"depends":["sourceMap.sourceId","sourceMap.schema"],"name":"table","type":"string","title":"表名","required":true,"props":{"method":"post","name":"sourcemap_table","transformer":"table","optionsFromRequest":true,"placeholder":"请选择表名","params":{"sourceId":"{{form#sourceMap.sourceId}}","schema":"{{form#sourceMap.schema}}","isRead":true,"isSys":false},"url":"/taier/api/dataSource/addDs/tablelist","required":["sourceId"]}},{"widget":"select","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"1,2,3,4,25"},{"field":"form.sourceMap.syncModel","isNot":true,"value":"1"}],"depends":["sourceMap.table"],"name":"increColumn","type":"string","title":"增量标识字段","required":true,"props":{"method":"post","name":"sourcemap_increColumn","transformer":"incrementColumn","optionsFromRequest":true,"placeholder":"请选择增量标识字段","params":{"sourceId":"{{form#sourceMap.sourceId}}","schema":"{{form#sourceMap.schema}}","tableName":"{{form#sourceMap.table}}"},"url":"/taier/api/task/getIncreColumn","required":["sourceId","tableName"]}},{"widget":"InputWithColumns","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"6,9"}],"name":"path","type":"string","title":"路径","rules":[{"required":true,"message":"请输入路径"},{"max":200,"message":"路径不得超过200个字符！"}],"props":{"placeholder":"例如: /rdos/batch"}},{"widget":"select","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"9"}],"name":"fileType|FTP","type":"string","title":"解析方式","initialValue":"txt","required":true,"props":{"allowClear":false,"options":[{"label":"CSV","value":"csv"},{"label":"Excel","value":"excel"},{"label":"TXT","value":"txt"}],"placeholder":"请选择解析方式"}},{"widget":"select","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"8,9"}],"name":"encoding","type":"string","title":"编码","initialValue":"utf-8","required":true,"props":{"allowClear":false,"options":[{"label":"utf-8","value":"utf-8"},{"label":"gdb","value":"gdb"}],"placeholder":"请选择编码"}},{"hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"8"}],"name":"startRowkey","type":"string","title":"开始行健","props":{"placeholder":"请输入开始行健"}},{"hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"8"}],"name":"endRowkey","type":"string","title":"结束行健","props":{"placeholder":"请输入结束行健"}},{"widget":"radio","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"8"}],"name":"isBinaryRowkey","type":"string","title":"行健二进制转换","initialValue":"0","props":{"options":[{"label":"FALSE","value":"0"},{"label":"TRUE","value":"1"}]}},{"widget":"inputNumber","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"8"}],"name":"scanCacheSize","type":"string","title":"每次RPC请求获取行数","props":{"min":0,"placeholder":"请输入大小, 默认为256","suffix":"行"}},{"widget":"inputNumber","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"8"}],"name":"scanBatchSize","type":"string","title":"每次RPC请求获取列数","props":{"min":0,"placeholder":"请输入大小, 默认为100","suffix":"列"}},{"widget":"textarea","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"1,2,3,4,25"}],"name":"where","rules":[{"max":1000,"message":"过滤语句不可超过1000个字符!"}],"type":"string","title":"数据过滤","props":{"autoSize":{"minRows":2,"maxRows":6},"placeholder":"请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步"}},{"widget":"select","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"1,2,3,4,25"}],"depends":["sourceMap.table"],"name":"split","type":"string","title":"切分键","props":{"method":"post","name":"sourcemap_split","transformer":"split","optionsFromRequest":true,"placeholder":"请选择切分键","params":{"sourceId":"{{form#sourceMap.sourceId}}","schema":"{{form#sourceMap.schema}}","tableName":"{{form#sourceMap.table#toArray}}"},"url":"/taier/api/dataSource/addDs/columnForSyncopate","required":["sourceId","tableName"]}},{"widget":"select","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"6"}],"name":"fileType","type":"string","title":"文件类型","initialValue":"text","required":true,"props":{"options":[{"label":"orc","value":"orc"},{"label":"text","value":"text"},{"label":"parquet","value":"parquet"}],"placeholder":"请选择文件类型"}},{"hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"6"},{"field":"form.sourceMap.fileType","isNot":true,"value":"text"}],"name":"fieldDelimiter","type":"string","title":"列分隔符","props":{"placeholder":"若不填写，则默认为\\\\001"}},{"name":"fieldDelimiter|FTP","type":"string","title":"列分隔符","required":true,"hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"9"}],"initialValue":",","props":{"placeholder":"若不填写，则默认为,"}},{"widget":"autoComplete","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"7,27,45,50"}],"depends":["sourceMap.table"],"name":"partition","type":"string","title":"分区","props":{"method":"post","name":"sourcemap_partition","transformer":"table","optionsFromRequest":true,"placeholder":"请填写分区信息","params":{"sourceId":"{{form#sourceMap.sourceId}}","tableName":"{{form#sourceMap.table}}"},"url":"/taier/api/dataSource/addDs/getHivePartitions","required":["sourceId","tableName"]}},{"widget":"select","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"11,33,46"}],"depends":["sourceMap.sourceId"],"name":"index","type":"string","title":"index","required":true,"props":{"method":"post","name":"sourcemap_schema","transformer":"table","optionsFromRequest":true,"placeholder":"请选择index","params":{"sourceId":"{{form#sourceMap.sourceId}}"},"url":"/taier/api/dataSource/addDs/getAllSchemas","required":["sourceId"]}},{"widget":"select","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"11,33"}],"depends":["sourceMap.index"],"name":"indexType","type":"string","title":"type","required":true,"props":{"method":"post","name":"sourcemap_table","transformer":"table","optionsFromRequest":true,"placeholder":"请选择indexType！","params":{"sourceId":"{{form#sourceMap.sourceId}}","schema":"{{form#sourceMap.schema}}","isRead":true,"isSys":false},"url":"/taier/api/dataSource/addDs/tablelist","required":["sourceId","schema"]}},{"widget":"textarea","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"11,33,46"}],"name":"query","rules":[{"max":1024,"message":"仅支持1-1024个任意字符"}],"type":"string","title":"query","props":{"autoSize":{"minRows":2,"maxRows":6},"placeholder":"\\"match_all\\":{}\\""}},{"widget":"radio","hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"9"}],"name":"isFirstLineHeader","type":"string","title":"是否包含表头","initialValue":false,"required":true,"props":{"options":[{"label":"是","value":true},{"label":"否","value":false}]}},{"widget":"textarea","hidden":[{"field":"form.sourceMap.sourceId","value":"undefined"}],"name":"extralConfig","validator":"json","type":"string","title":"高级配置","props":{"autoSize":{"minRows":2,"maxRows":6},"placeholder":"以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize"}},{"hidden":true,"name":"column","type":"string","title":"列"}],"name":"sourceMap","type":"object","title":"数据来源"},{"children":[{"bind":{"field":"targetMap.sourceId","transformer":"{{optionCollections.targetmap_sourceId#find.type}}"},"name":"type","type":"number","title":"类型","noStyle":true},{"widget":"select","name":"sourceId","type":"number","title":"数据源","required":true,"props":{"method":"get","name":"targetmap_sourceId","transformer":"sourceIdOnWriter","optionsFromRequest":true,"placeholder":"请选择数据源","url":"/taier/api/dataSource/manager/total"}},{"widget":"select","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"2,4,64"}],"depends":["targetMap.sourceId"],"name":"schema","type":"number","title":"schema","props":{"method":"post","name":"targetmap_schema","transformer":"table","optionsFromRequest":true,"placeholder":"请选择 schema","params":{"sourceId":"{{form#targetMap.sourceId}}"},"url":"/taier/api/dataSource/addDs/getAllSchemas","required":["sourceId"]}},{"widget":"SelectWithCreate","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"1,2,3,4,7,8,25,27,45,50,64"}],"depends":["targetMap.sourceId","targetMap.schema"],"name":"table","type":"string","title":"表名","required":true,"props":{"method":"post","name":"targetmap_table","transformer":"table","optionsFromRequest":true,"placeholder":"请选择表名","params":{"sourceId":"{{form#targetMap.sourceId}}","schema":"{{form#targetMap.schema}}","isRead":true,"isSys":false},"url":"/taier/api/dataSource/addDs/tablelist","required":["sourceId"]}},{"widget":"autoComplete","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"7,27,45,50"}],"depends":["targetMap.table"],"name":"partition","type":"string","title":"分区","props":{"method":"post","name":"targetmap_partition","transformer":"table","optionsFromRequest":true,"placeholder":"请填写分区信息","params":{"sourceId":"{{form#targetMap.sourceId}}","tableName":"{{form#targetMap.table}}"},"url":"/taier/api/dataSource/addDs/getHivePartitions","required":["sourceId","tableName"]}},{"widget":"textarea","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"1,2,3,4,25,64"}],"depends":["targetMap.type"],"name":"preSql","type":"string","title":"导入前准备语句","props":{"autoSize":{"minRows":2,"maxRows":6},"placeholder":"请输入导入数据前执行的 SQL 脚本"}},{"widget":"textarea","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"1,2,3,4,25,64"}],"depends":["targetMap.type"],"name":"postSql","type":"string","title":"导入后准备语句","props":{"autoSize":{"minRows":2,"maxRows":6},"placeholder":"请输入导入数据后执行的 SQL 脚本"}},{"hidden":[{"field":"form.targetMap.type","isNot":true,"value":"6,9"}],"name":"path","rules":[{"max":200,"message":"路径不得超过200个字符！"}],"type":"string","title":"路径","required":true,"props":{"placeholder":"例如: /app/batch"}},{"hidden":[{"field":"form.targetMap.type","isNot":true,"value":"6"}],"name":"fileName","type":"string","title":"文件名","required":true,"props":{"placeholder":"请输入文件名"}},{"widget":"select","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"6"}],"name":"fileType","type":"string","title":"文件类型","initialValue":"orc","required":true,"props":{"options":[{"label":"orc","value":"orc"},{"label":"text","value":"text"},{"label":"parquet","value":"parquet"}],"placeholder":"请选择文件类型"}},{"hidden":[{"field":"form.targetMap.type","isNot":true,"value":"6,9"}],"name":"fieldDelimiter","type":"string","title":"列分隔符","initialValue":",","props":{"placeholder":"例如: 目标为 hive 则分隔符为\\001"}},{"widget":"select","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"6,8,9"}],"name":"encoding","type":"string","title":"编码","initialValue":"utf-8","required":true,"props":{"allowClear":false,"options":[{"label":"utf-8","value":"utf-8"},{"label":"gdb","value":"gdb"}],"placeholder":"请选择编码"}},{"widget":"radio","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"2,4,6,7,9,25,27,45,50"}],"depends":["targetMap.sourceId"],"name":"writeMode","type":"string","title":"写入模式","required":true,"props":{"options":[{"label":"覆盖（Insert Overwrite）","value":"replace"},{"label":"追加（Insert Into）","value":"insert"}]}},{"widget":"radio","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"8"}],"name":"nullMode","type":"string","title":"读取为空时的处理方式","initialValue":"skip","props":{"options":[{"label":"SKIP","value":"skip"},{"label":"EMPTY","value":"empty"}]}},{"widget":"inputNumber","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"8"}],"name":"writeBufferSize","type":"string","title":"写入缓存大小","props":{"placeholder":"请输入缓存大小","suffix":"KB"}},{"widget":"select","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"11,33,46"}],"depends":["targetMap.sourceId"],"name":"index","type":"string","title":"index","required":true,"props":{"method":"post","name":"targetmap_schema","transformer":"table","optionsFromRequest":true,"placeholder":"请选择index","params":{"sourceId":"{{form#targetMap.sourceId}}"},"url":"/taier/api/dataSource/addDs/getAllSchemas","required":["sourceId"]}},{"widget":"select","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"11,33"}],"depends":["targetMap.index"],"name":"indexType","type":"string","title":"type","required":true,"props":{"method":"post","name":"targetmap_table","transformer":"table","optionsFromRequest":true,"placeholder":"请选择indexType！","params":{"sourceId":"{{form#targetMap.sourceId}}","schema":"{{form#targetMap.schema}}","isRead":true,"isSys":false},"url":"/taier/api/dataSource/addDs/tablelist","required":["sourceId","schema"]}},{"widget":"inputNumber","hidden":[{"field":"form.targetMap.type","isNot":true,"value":"11,33,46"}],"name":"bulkAction","type":"number","title":"bulkAction","initialValue":100,"required":true,"props":{"min":1,"max":200000,"precision":0,"placeholder":"请输入 bulkAction"}},{"widget":"textarea","hidden":[{"field":"form.targetMap.sourceId","value":"undefined"}],"name":"extralConfig","validator":"json","type":"string","title":"高级配置","props":{"autoSize":{"minRows":2,"maxRows":6},"placeholder":"以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize"}},{"hidden":true,"name":"column","type":"string","title":"列"}],"name":"targetMap","type":"object","title":"选择目标"},{"children":[{"widget":"KeyMap","type":"any"}],"name":"mapping","type":"object","title":"字段映射"},{"children":[{"widget":"autoComplete","name":"speed","type":"string","title":"作业速率上限","initialValue":"不限制传输速率","required":true,"props":{"options":[{"value":"不限制传输速率"},{"value":"1"},{"value":"2"},{"value":"3"},{"value":"4"},{"value":"5"},{"value":"6"},{"value":"7"},{"value":"8"},{"value":"9"},{"value":"10"}],"placeholder":"请选择作业速率上限","suffix":"MB/s"}},{"widget":"autoComplete","name":"channel","type":"string","title":"作业并发数","initialValue":"1","required":true,"props":{"options":[{"value":"1"},{"value":"2"},{"value":"3"},{"value":"4"},{"value":"5"}],"placeholder":"请选择作业并发数"}},{"hidden":[{"field":"form.sourceMap.type","isNot":true,"value":"1,2,3,4,8,19,22,24,25,28,29,31,32,35,36,40,53,54,61,71,73"},{"field":"form.targetMap.type","isNot":true,"value":"1,2,3,4,7,8,10,19,22,24,25,27,28,29,31,32,35,36,40,53,54,61,71,73"}],"name":"isRestore","type":"boolean","title":"断点续传"},{"widget":"select","hidden":[{"field":"form.settingMap.isRestore","value":"false,undefined"}],"name":"restoreColumnName","type":"string","title":"标识字段","required":true,"props":{"method":"post","name":"settingmap_restore","transformer":"restore","optionsFromRequest":true,"placeholder":"请选择标识字段","params":{"sourceId":"{{form#sourceMap.sourceId}}","schema":"{{form#sourceMap.schema}}","tableName":"{{form#sourceMap.table}}"},"url":"/taier/api/task/getIncreColumn","required":["sourceId","tableName"]}}],"name":"settingMap","type":"object","title":"通道控制"}],"type":"object"}'
WHERE `type` = 17
  and dict_code = 'SYNC';

-- supported mysql task
-- source comment of the "增加'MySQL','Greenplum','GaussDB','PostgreSQL','SQLServer','TiDB','Vertica','MaxCompute' 任务"
DELETE
FROM `dict`
WHERE type = 30
  AND dict_name IN
      ('MySQL', 'Greenplum', 'GaussDB', 'PostgreSQL', 'SQLServer', 'TiDB', 'Vertica', 'MaxCompute', 'HadoopMR');
INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                    gmt_create, gmt_modified, is_deleted)
VALUES ('17', 'MySQL',
        '{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":[1]}',
        '', 30, 0, 'STRING', '', 0, '2022-10-24 15:46:53', '2022-10-24 15:46:53', 0);
INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                    gmt_create, gmt_modified, is_deleted)
VALUES ('18', 'Greenplum',
        '{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":[36]}',
        '', 30, 0, 'STRING', '', 0, '2022-10-24 15:46:53', '2022-10-24 15:46:53', 0);
INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                    gmt_create, gmt_modified, is_deleted)
VALUES ('19', 'GaussDB',
        '{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":[21]}',
        '', 30, 0, 'STRING', '', 0, '2022-10-24 15:46:53', '2022-10-24 15:46:53', 0);
INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                    gmt_create, gmt_modified, is_deleted)
VALUES ('20', 'PostgreSQL',
        '{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":[4]}',
        '', 30, 0, 'STRING', '', 0, '2022-10-24 15:46:53', '2022-10-24 15:46:53', 0);
INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                    gmt_create, gmt_modified, is_deleted)
VALUES ('21', 'SQLServer',
        '{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":[3]}',
        '', 30, 0, 'STRING', '', 0, '2022-10-24 15:46:53', '2022-10-24 15:46:53', 0);
INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                    gmt_create, gmt_modified, is_deleted)
VALUES ('22', 'TiDB',
        '{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":[31]}',
        '', 30, 0, 'STRING', '', 0, '2022-10-24 15:46:53', '2022-10-24 15:46:53', 0);
INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                    gmt_create, gmt_modified, is_deleted)
VALUES ('23', 'Vertica',
        '{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":[43]}',
        '', 30, 0, 'STRING', '', 0, '2022-10-24 15:46:53', '2022-10-24 15:46:53', 0);
INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                    gmt_create, gmt_modified, is_deleted)
VALUES ('24', 'MaxCompute',
        '{"actions":["SAVE_TASK","RUN_TASK","STOP_TASK","SUBMIT_TASK","OPERATOR_TASK"],"barItem":["task","dependency","task_params","env_params"],"formField":["datasource"],"renderKind":"editor","dataTypeCodes":[10]}',
        '', 30, 0, 'STRING', '', 0, '2022-10-24 15:46:53', '2022-10-24 15:46:53', 0);

UPDATE `dict`
SET dict_value = '112'
WHERE dict_code = 'flink_version'
  and dict_name = '1.12-standalone'
limit 1;

UPDATE `console_component_config`
SET `value` = replace(value, 'hdfs://ns1', 'hdfs://')
WHERE cluster_id = -2
  AND `value` LIKE 'hdfs://ns1%';

ALTER TABLE develop_hive_select_sql
    ADD datasource_id int default 0 null comment '数据源id';
RENAME TABLE develop_hive_select_sql TO develop_select_sql;

-- delete unused table
DROP TABLE IF EXISTS develop_read_write_lock;
DROP TABLE IF EXISTS develop_tenant_component;
DROP TABLE IF EXISTS task_template;
DROP TABLE IF EXISTS schedule_plugin_job_info;
RENAME TABLE schedule_engine_job_cache TO schedule_job_cache;
RENAME TABLE schedule_engine_job_retry TO schedule_job_retry;



INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                    gmt_create, gmt_modified, is_deleted)
VALUES ('25', 'HadoopMR',
        '{"actions": ["SAVE_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "formField": ["resourceIdList", "mainClass", "exeArgs"],"barItem":["dependency","task_params","env_params"], "renderKind": "spark"}',
        '', 30, 5, 'STRING', '', 0, '2023-02-09 10:28:45', '2023-02-09 10:28:45', 0);

DELETE
FROM `dict`
WHERE type = 18;
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '6', null, 18, 1, 'STRING', 'Apache Hadoop 2.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '63', null, 18, 1, 'STRING', 'Apache Hadoop 3.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '6', null, 18, 1, 'STRING', 'HDP 2.6.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '6', null, 18, 1, 'STRING', 'HDP 3.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '6', null, 18, 1, 'STRING', 'CDH 5.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '6', null, 18, 1, 'STRING', 'CDH 6.0.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '6', null, 18, 1, 'STRING', 'CDH 6.1.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '6', null, 18, 1, 'STRING', 'CDH 6.2.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '1003', null, 18, 1, 'STRING', 'CDP 7.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '6', null, 18, 1, 'STRING', 'TDH 5.2.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '6', null, 18, 1, 'STRING', 'TDH 7.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '2', '6', null, 18, 1, 'STRING', 'TDH 6.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '80', null, 18, 1, 'STRING', 'Apache Hadoop 2.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '81', null, 18, 1, 'STRING', 'Apache Hadoop 3.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '80', null, 18, 1, 'STRING', 'HDP 2.6.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '81', null, 18, 1, 'STRING', 'HDP 3.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '80', null, 18, 1, 'STRING', 'CDH 5.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '80', null, 18, 1, 'STRING', 'CDH 6.0.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '80', null, 18, 1, 'STRING', 'CDH 6.1.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '80', null, 18, 1, 'STRING', 'CDH 6.2.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '81', null, 18, 1, 'STRING', 'CDP 7.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '80', null, 18, 1, 'STRING', 'TDH 5.2.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '80', null, 18, 1, 'STRING', 'TDH 7.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('component_datasource_mapping', '3', '80', null, 18, 1, 'STRING', 'TDH 6.x', 0, '2023-04-01 10:19:00',
        '2023-04-01 10:19:00', 0);

ALTER TABLE console_component
    ADD datasource_type int null comment '数据插件类型';

-- script 支持 standalone
UPDATE console_component t
SET t.version_name = 'on-yarn',
    t.gmt_modified = now(),
    t.deploy_type = 1
WHERE t.component_type_code = 8;

-- 处理组件配置是否有多版本之类的
update dict
set dict_value = '{"owner": "COMPUTE", "dependsOn": ["RESOURCE", "STORAGE"], "allowKerberos": "true", "allowCoexistence": true, "uploadConfigType": "0", "versionDictionary": "SCRIPT_VERSION"}'
where type = 12
  and dict_name = 'SCRIPT';

DELETE
FROM `dict`
WHERE type = 34
  and dict_code = 'script_version';
-- 处理组件配置树
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('script_version', 'on-yarn', '', null, 34, 2, 'INTEGER', '', 0, now(), now(), 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('script_version', 'standalone', '', null, 34, 2, 'INTEGER', '', 0, now(), now(), 0);

DELETE
FROM `dict`
WHERE dict_name = 'script-standalone'
  and dict_code = 'typename_mapping';
-- 处理组件默认版本
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name,
                  is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'script-standalone', '-320', null, 6, 0, 'LONG', '', 0, now(), now(), 0);

DELETE
FROM `dict`
WHERE dict_name = 'standalone'
  and dict_code = 'component_model_config'
  and type = 14;
-- 处理组件配置模版获取
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name,
                  is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'standalone', '{"standalone":"script-standalone"}', null, 14, 1,
        'STRING', 'SCRIPT', 0, now(), now(), 0);

DELETE
FROM `console_component_config`
WHERE `cluster_id` = -2
  AND `component_id` IN (-320, -233);
-- 组件模版参数
insert into console_component_config (cluster_id, component_id, component_type_code, type, required, `key`,
                                      value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create,
                                      gmt_modified, is_deleted)
values (-2, -320, 8, 'INPUT', 1, 'script.python3.path', '/data/miniconda3/bin/python3', null, null, null, null, now(),
        now(), 0),
       (-2, -320, 8, 'INPUT', 1, 'script.python2.path', '/data/miniconda3/bin/python2', null, null, null, null, now(),
        now(), 0),
       (-2, -320, 8, 'INPUT', 1, 'execute.dir', '/tmp/dir', null, null, null, null, now(), now(), 0);

-- 索引处理
alter table console_component
drop key index_component;

alter table console_component
    add key console_component_pk (cluster_id, component_type_code, version_value);

-- 组件配置参数注释
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'log.dir', '临时脚本、运行日志存放路径', '8', 25, 0, 'STRING', '', 0, now(), now(), 0);

UPDATE task_param_template t
SET t.params = '## 任务运行方式：
## yarn: 将任务运行在Hadoop集群上
## standalone: 将任务运行在本地，单独运行
runMode=yarn
## 每个worker所占内存，比如512m
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
logLevel=INFO'
WHERE t.task_type in (12, 13);

DELETE
FROM `dict`
WHERE dict_code = 'component_model'
  and dict_name = 'DATAX';

INSERT INTO `dict` (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                    gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'DATAX',
        '{"owner": "COMPUTE", "dependsOn": [], "nameTemplate": "DATAX", "allowKerberos": "false", "allowCoexistence": false, "uploadConfigType": "0"}',
        null, 12, 0, 'STRING', '', 0, '2023-02-07 11:26:57', '2023-02-07 16:54:54', 0);

DELETE
FROM `dict`
WHERE dict_code = 'typename_mapping'
  and dict_name = 'DATAX';
-- 处理组件默认版本
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name,
                  is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'DATAX', '-233', null, 6, 0, 'LONG', '', 0, now(), now(), 0);

-- 组件模版参数
insert into console_component_config (cluster_id, component_id, component_type_code, type, required, `key`,
                                      value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create,
                                      gmt_modified, is_deleted)
values (-2, -233, 8, 'INPUT', 1, 'DataX.local.path', '/data/taier', null, null, null, null, now(), now(), 0);

insert into console_component_config (cluster_id, component_id, component_type_code, type, required, `key`,
                                      value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create,
                                      gmt_modified, is_deleted)
values (-2, -233, 8, 'INPUT', 1, 'DataX.task.temp', '/data/taier', null, null, null, null, now(), now(), 0),
       (-2, -233, 8, 'INPUT', 1, 'execute.dir', '/tmp/dir', null, null, null, null, now(), now(), 0),
       (-2, -233, 8, 'INPUT', 1, 'DATAX.python.path', 'python3', null, null, null, null, now(), now(), 0);

DELETE
FROM `dict`
WHERE dict_code = '26'
  and dict_name = 'DATAX';

INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                  gmt_create, gmt_modified, is_deleted)
VALUES ('26', 'DATAX',
        '{"actions": ["SAVE_TASK", "RUN_TASK", "STOP_TASK", "SUBMIT_TASK", "OPERATOR_TASK"], "barItem": ["task", "dependency", "task_params", "env_params"], "renderKind": "editor","dataTypeCodes":["27","7","50"]}',
        null, 30, 0, 'STRING', '', 0, '2023-03-03 07:27:25', '2022-03-03 07:27:25', 0);
DELETE
FROM `dict`
WHERE type = 14
  and depend_name = 'DATAX';
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'standalone', '{"standalone":"DataX"}', null, 14, 1, 'STRING', 'DATAX', 0, '2023-05-07 11:44:33', '2023-05-07 11:44:33', 0);
COMMIT;