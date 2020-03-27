CREATE TABLE `schedule_plugin_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `plugin_key` varchar(255) NOT NULL COMMENT '插件配置信息md5值',
  `plugin_info` text NOT NULL COMMENT '插件信息',
  `type` tinyint(2) NOT NULL COMMENT '类型 0:默认插件, 1:动态插件(暂时数据库只存动态插件)',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_plugin_id` (`plugin_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `schedule_engine_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
  `job_id` varchar(256) NOT NULL COMMENT '离线任务id',
  `engine_job_id` varchar(256) DEFAULT NULL COMMENT '离线任务计算引擎id',
  `application_id` varchar(256) DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id',
  `job_name` varchar(256) DEFAULT NULL COMMENT '任务名称',
  `exec_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `exec_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `exec_time` int(11) DEFAULT '0' COMMENT '执行时间',
  `retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '执行时，重试的次数',
  `log_info` mediumtext COMMENT '错误信息',
  `engine_log` longtext COMMENT '引擎错误信息',
  `plugin_info_id` int(11) DEFAULT NULL COMMENT '插件信息',
  `source_type` tinyint(2) DEFAULT NULL COMMENT '任务来源',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `version_id` int(11) DEFAULT NULL COMMENT '任务对应版本id',
  `retry_task_params` text DEFAULT NULL COMMENT '重试任务参数',
  `compute_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '计算类型STREAM(0), BATCH(1)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`(128),`is_deleted`),
  KEY `index_engine_job_id` (`engine_job_id`(128)),
  KEY `index_status` (`status`),
  KEY `index_gmt_modified` (`gmt_modified`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `schedule_engine_job_checkpoint` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(64) NOT NULL COMMENT '任务id',
  `task_engine_id` varchar(64) NOT NULL COMMENT '任务对于的引擎id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `checkpoint_id` varchar(64) DEFAULT NULL COMMENT '检查点id',
  `checkpoint_trigger` timestamp NULL DEFAULT NULL COMMENT 'checkpoint触发时间',
  `checkpoint_savepath` varchar(128) DEFAULT NULL COMMENT 'checkpoint存储路径',
  `checkpoint_counts` varchar(128) DEFAULT NULL COMMENT 'checkpoint信息中的counts指标',
  PRIMARY KEY (`id`),
  UNIQUE KEY `taskid_checkpoint` (`task_id`,`checkpoint_id`) COMMENT 'taskid和checkpoint组成的唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=26474 DEFAULT CHARSET=utf8;

CREATE TABLE `schedule_engine_job_cache` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(256) NOT NULL COMMENT '任务id',
  `job_name` VARCHAR(256) DEFAULT NULL COMMENT '任务名称',
  `engine_type` varchar(256) NOT NULL COMMENT '任务的执行引擎类型',
  `compute_type` tinyint(2) NOT NULL COMMENT '计算类型stream/batch',
  `stage` tinyint(2) NOT NULL COMMENT '处于master等待队列：1 还是exe等待队列 2',
  `job_info` longtext NOT NULL COMMENT 'job信息',
  `node_address` varchar(256) DEFAULT NULL COMMENT '节点地址',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `job_priority` BIGINT(20) DEFAULT NULL COMMENT '任务优先级',
  `job_resource` VARCHAR(256) DEFAULT NULL COMMENT 'job的计算引擎资源类型',
  `is_failover` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0：不是，1：由故障恢复来的任务',
  PRIMARY KEY (`id`),
  unique KEY `index_job_id` (`job_id`(128))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `schedule_plugin_job_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(255) NOT NULL COMMENT '任务id',
  `job_info` LONGTEXT NOT NULL COMMENT '任务信息',
  `log_info` text COMMENT '任务信息',
  `status` tinyint(2) NOT NULL COMMENT '任务状态',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `schedule_engine_unique_sign` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unique_sign` varchar(255) NOT NULL COMMENT '唯一标识',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_unique_sign` (`unique_sign`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 重试记录表
CREATE TABLE `schedule_engine_job_retry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
  `job_id` varchar(256) NOT NULL COMMENT '离线任务id',
  `engine_job_id` varchar(256) DEFAULT NULL COMMENT '离线任务计算引擎id',
  `application_id` varchar(256) DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id',
  `exec_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `exec_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '执行时，重试的次数',
  `log_info` mediumtext COMMENT '错误信息',
  `engine_log` longtext COMMENT '引擎错误信息',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `retry_task_params` text DEFAULT NULL COMMENT '重试任务参数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `schedule_engine_job_stop_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(256) NOT NULL COMMENT '任务id',
  `task_type` int(10) DEFAULT NULL COMMENT '任务类型',
  `engine_type` varchar(256) DEFAULT NULL COMMENT '任务的执行引擎类型',
  `compute_type` tinyint(2) DEFAULT NULL COMMENT '计算类型stream/batch',
  `job_resource` VARCHAR(256) DEFAULT NULL COMMENT 'job的计算引擎资源类型',
  `version` int(10) DEFAULT '0' COMMENT '版本号',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `operator_expired` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作过期时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `schedule_node_machine` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(64) NOT NULL COMMENT 'master主机ip',
  `port` int(11) NOT NULL COMMENT 'master主机端口',
  `machine_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 master,1 slave',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `app_type` varchar(64) NOT NULL DEFAULT 'web' COMMENT 'web,engine',
  `deploy_info` varchar(256) DEFAULT NULL COMMENT 'flink,spark对应的部署模式',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_machine` (`ip`,`port`)
) ENGINE=InnoDB AUTO_INCREMENT=1018 DEFAULT CHARSET=utf8