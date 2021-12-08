CREATE TABLE `schedule_job_failed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uic_tenant_id` int(11) NOT NULL COMMENT '租户id',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `app_type` int(11) NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `error_count` int(11) DEFAULT '0' COMMENT '计数值',
  PRIMARY KEY (`id`),
  KEY `index_task_id` (`task_id`,`app_type`,`gmt_create`),
  KEY `index_create` (`gmt_create`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('DAG_JOB_ERROR_FIRST', 'FAILED_OPEN', 'false', '', 0, 1, 'STRING', null, 0, now(), now(), 0);