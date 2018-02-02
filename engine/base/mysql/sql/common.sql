DROP TABLE IF EXISTS `rdos_plugin_mysql_job_info`;
CREATE TABLE `rdos_plugin_mysql_job_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(255) NOT NULL COMMENT '任务id',
  `job_info` text NOT NULL COMMENT '任务信息',
  `log_info` text COMMENT '任务信息',
  `status` tinyint(2) NOT NULL COMMENT '任务状态',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;