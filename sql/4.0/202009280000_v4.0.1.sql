-- console 的迁移表
CREATE TABLE `dt_alert_gate` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `alert_gate_name` varchar(32) DEFAULT NULL,
  `alert_gate_type` smallint(2) DEFAULT NULL,
  `alert_gate_code` varchar(16) DEFAULT NULL,
  `alert_gate_status` smallint(2) DEFAULT NULL,
  `alert_gate_json` varchar(1024) DEFAULT NULL,
  `is_deleted` smallint(2) DEFAULT NULL,
  `gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `alert_gate_source` varchar(32) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `dt_alert_template` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `alert_template_name` varchar(32) DEFAULT NULL,
  `alert_template_type` smallint(2) DEFAULT NULL,
  `alert_template_status` smallint(2) DEFAULT NULL,
  `alert_template` text,
  `is_deleted` smallint(2) DEFAULT NULL,
  `gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `alert_gate_source` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `dt_cluster_alert` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `cluster_id` int(11) unsigned NOT NULL,
  `alert_id` int(11) unsigned NOT NULL,
  `is_default` tinyint(3) NOT NULL,
  `gmt_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `dt_notify_record_content` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT '租户id',
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `app_type` int(11) NOT NULL COMMENT '应用类型，1：RDOS, 2:数据质量, 3:数据API ,4: 标签工程 ,5:数据地图',
  `content` text NOT NULL COMMENT '内容文本',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '触发类型',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='消息记录内容';

CREATE TABLE `dt_notify_record_read` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT '租户id',
  `project_id` int(11) DEFAULT '0' COMMENT '项目id',
  `app_type` int(11) NOT NULL COMMENT '应用类型，1：RDOS, 2:数据质量, 3:数据API ,4: 标签工程 ,5:数据地图',
  `notify_record_id` int(11) NOT NULL DEFAULT '0' COMMENT '通知记录id',
  `content_id` int(11) NOT NULL DEFAULT '0' COMMENT '内容文本id',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '接收人id',
  `read_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未读 1:已读',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_app_type_notify_record_id` (`app_type`,`notify_record_id`),
  KEY `idx_app_type_user_id` (`app_type`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='消息记录读状态';

CREATE TABLE `dt_notify_send_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT '租户id',
  `project_id` int(11) DEFAULT '0' COMMENT '项目id',
  `app_type` int(11) NOT NULL COMMENT '应用类型，1：RDOS, 2:数据质量, 3:数据API ,4: 标签工程 ,5:数据地图',
  `notify_record_id` int(11) NOT NULL COMMENT '通知记录id',
  `content_id` int(11) NOT NULL DEFAULT '0' COMMENT '内容文本id',
  `user_id` int(11) NOT NULL COMMENT '发送的用户id',
  `send_type` tinyint(1) NOT NULL COMMENT '1：邮件，2: 短信，3: 微信，4: 钉钉',
  `send_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:准备发送 1:发送成功 2:发送失败',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='通知记录表';

CREATE TABLE `console_security_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) NOT NULL COMMENT '当前app下的用户租户id',
  `operator` varchar(100) NOT NULL COMMENT '操作人',
  `operator_id` bigint(20) NOT NULL COMMENT '操作人在对应app下的用户id',
  `app_tag` varchar(45) NOT NULL COMMENT 'App类型标示',
  `action` varchar(200) NOT NULL COMMENT '执行的动作',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `operation` varchar(10) NOT NULL COMMENT '当前操作',
  `operation_object` varchar(200) NOT NULL COMMENT '操作对象',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `task_param_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `compute_type` int(11) DEFAULT NULL,
  `engine_type` int(11) DEFAULT NULL,
  `task_type` int(11) DEFAULT '0' COMMENT '默认0-任务类型',
  `params` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_compute_engine_task` (`compute_type`,`engine_type`,`task_type`,`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

insert IGNORE into task_param_template
select * from console.task_param_template;

insert IGNORE into console_security_log
select * from console.console_security_log;

insert IGNORE into dt_notify_send_record
select * from console.dt_notify_send_record;

insert IGNORE into dt_notify_record_content
select * from console.dt_notify_record_content;

insert IGNORE into dt_alert_gate
select * from console.dt_alert_gate;

insert IGNORE into dt_alert_template
select * from console.dt_alert_template;

insert IGNORE into dt_cluster_alert
select * from console.dt_cluster_alert;

insert IGNORE into dt_notify_record_read
select * from console.dt_notify_record_read;

