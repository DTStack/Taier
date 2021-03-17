CREATE TABLE `schedule_task_commit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `app_type` int(11) NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `commit_id` varchar(128) NOT NULL COMMENT '提交id',
  `task_json` text COMMENT '额外参数',
  `extra_info` mediumtext COMMENT '存储task运行时所需的额外信息',
  `is_commit` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否提交：0未提交 1已提交',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '过期策略：0永不过期 1过期取消',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`commit_id`,`is_deleted`,`task_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
