CREATE TABLE `rdos_engine_job_stop_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(256) NOT NULL COMMENT '任务id',
  `task_type` int(10) NOT NULL COMMENT '任务类型',
  `engine_type` varchar(256) NOT NULL COMMENT '任务的执行引擎类型',
  `compute_type` tinyint(2) NOT NULL COMMENT '计算类型stream/batch',
  `group_name` VARCHAR(256) DEFAULT NULL COMMENT 'group name',
  `version` int(10) DEFAULT '0' COMMENT '版本号',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
