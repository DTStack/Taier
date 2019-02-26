alter table rdos_engine_job_cache add COLUMN job_priority BIGINT(20) DEFAULT NULL COMMENT '任务优先级';
alter table rdos_engine_job_cache add COLUMN group_name VARCHAR(256) DEFAULT NULL COMMENT 'group name';



CREATE TABLE `rdos_engine_batch_job_retry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
  `job_id` varchar(256) NOT NULL COMMENT '离线任务id',
  `engine_job_id` varchar(256)  COMMENT '离线任务计算引擎id',
  `exec_start_time` datetime  COMMENT '执行开始时间',
  `exec_end_time` datetime  COMMENT '执行结束时间',
  `retry_num` int(10) NOT NULL DEFAULT '0',
  `log_info` mediumtext COMMENT '错误信息',
  `engine_log` longtext COMMENT '引擎错误信息',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `rdos_engine_stream_job_retry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
  `task_id` varchar(256) NOT NULL COMMENT '离线任务id',
  `engine_task_id` varchar(256)  COMMENT '离线任务计算引擎id',
  `application_id` varchar(256)  COMMENT '独立运行的任务需要记录额外的id',
  `exec_start_time` datetime  DEFAULT CURRENT_TIMESTAMP COMMENT '执行开始时间',
  `exec_end_time` datetime  DEFAULT CURRENT_TIMESTAMP COMMENT '执行结束时间',
  `retry_num` int(10) NOT NULL DEFAULT '0',
  `log_info` mediumtext COMMENT '错误信息',
  `engine_log` longtext COMMENT '引擎错误信息',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
