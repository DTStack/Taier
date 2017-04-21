CREATE TABLE `rdos_stream_action_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `action_type` tinyint(1) NOT NULL comment '操作类型 0启动 1停止',
  `status` tinyint(1) NOT NULL default 0 comment '操作状态 0 未开始，1操作成功 2操作失败',
  `task_id` varchar(256) NOT NULL comment '任务id',
  `is_restoration` tinyint(1) NOT NULL default 0 comment '0不恢复 1恢复',
  `gmt_create` datetime NOT NULL comment '新增时间',
  `gmt_modified` datetime NOT NULL comment '修改时间',
  `create_user_id` int(11) NOT NULL comment '发起操作的用户',
  `project_id` int(11) NOT NULL comment '项目id',
  `is_deleted` tinyint(1) NOT NULL default 0 comment '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_create_time` (`gmt_create`),
  KEY `index_task_id` (`task_id`,`is_deleted`),
  KEY `index_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rdos_stream_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `node_pid` int(11) NOT NULL COMMENT '父文件夹id',
  `task_id` varchar(256) NOT NULL comment '任务id',
  `engine_task_id` varchar(256) comment '执行引擎任务id',
  `name` varchar(256) NOT NULL comment '任务名称',
  `status` tinyint(1) NOT NULL comment '任务状态 	UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
  `task_type` tinyint(1) NOT NULL comment '任务类型 0 sql, 1 mr',
  `engine_type` tinyint(1) NOT NULL COMMENT '执行引擎类型 0 flink, 1 spark',
  `compute_type` tinyint(1) NOT NULL comment '计算类型 0实时，1 离线',
  `sql_text` text NOT NULL comment 'sql 文本',
  `task_params` text NOT NULL comment '任务参数',
  `gmt_create` datetime NOT NULL comment '新增时间',
  `gmt_modified` datetime NOT NULL comment '修改时间',
  `modify_user_id` int(11) NOT NULL comment '最后修改task的用户',
  `create_user_id` int(11) NOT NULL comment '新建task的用户',
  `project_id` int(11) NOT NULL comment '项目id',
  `version` int(11) NOT NULL default 0 comment 'task版本',
  `is_deleted` tinyint(1) NOT NULL default 0 comment '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_name` (`name`),
  KEY `index_task_id` (`task_id`(128),`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rdos_stream_server_log` (
  `id` INT(11)  NOT NULL AUTO_INCREMENT,
  `task_id` VARCHAR(256) COMMENT '任务id',
  `engine_task_id` VARCHAR(256) COMMENT '引擎任务id',
  `action_log_id` BIGINT(20) NOT NULL COMMENT '启动关联id',
  `project_id` int(11) NOT NULL comment '项目id',
  `log_info` MEDIUMTEXT NOT NULL COMMENT '错误信息',
  `gmt_create` datetime NOT NULL comment '新增时间',
  `gmt_modified` datetime NOT NULL comment '修改时间',
  `is_deleted` tinyint(1) NOT NULL default 0 comment '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_task_id` (`task_id`(128), `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rdos_stream_catalogue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `node_name` varchar(128) NOT NULL COMMENT '文件夹名称',
  `node_pid` int(11) NOT NULL DEFAULT -1 COMMENT '父文件夹id -1:说明是根目录',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `create_user_id` int(11) NOT NULL COMMENT '创建用户',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`node_id`),
  UNIQUE KEY `index_catologue_name` (`node_name`,`node_pid`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8

CREATE TABLE `rdos_stream_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `node_pid` int(11) NOT NULL COMMENT '父文件夹id',
  `url` varchar(1028) NOT NULL COMMENT '资源路径',
  `resource_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '资源类型 1,jar 2 file',
  `resource_name` varchar(256) NOT NULL COMMENT '资源名称',
  `origin_file_name` varchar(256) NOT NULL COMMENT '源文件名',
  `resource_desc` varchar(256) NOT NULL COMMENT '源文描述',
  `gmt_create` datetime NOT NULL COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `create_user_id` int(11) NOT NULL COMMENT '新建资源的用户',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_resource_name` (`resource_name`(128)),
  KEY `index_resource_type` (`resource_type`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8
