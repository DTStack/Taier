CREATE TABLE `schedule_engine_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `uic_tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT 'uic租户id',
  `app_type` int(11) NOT NULL DEFAULT '0' COMMENT '引用类型',
  `project_name` varchar(128) NOT NULL DEFAULT '' COMMENT '项目名',
  `project_alias` varchar(512) NOT NULL DEFAULT '' COMMENT '表中文名',
  `project_Identifier` varchar(256) DEFAULT '' COMMENT '项目标识',
  `project_desc` varchar(2048) DEFAULT '' COMMENT '项目描述',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '项目状态0：初始化，1：正常,2:禁用,3:失败',
  `create_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '新建项目的用户id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除，0未删除 1删除',
  PRIMARY KEY (`id`),
  KEY `index_project_id` (`project_id`),
  KEY `index_uic_tenant_id_and_app_type` (`uic_tenant_id`,`app_type`,`project_alias`),
  UNIQUE KEY `index_unique_project_id` (`project_id`,`app_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目表';


ALTER TABLE `schedule_task_shade` ADD  `task_rule` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务规则 0 默认无规则 1弱规则 2强规则';
ALTER TABLE `schedule_job` ADD  `task_rule` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务规则 0 默认无规则 1弱规则 2强规则';

ALTER TABLE `schedule_task_task_shade` ADD  `parent_app_type` int(11) NOT NULL DEFAULT '0' COMMENT '父任务的appType';

UPDATE `schedule_task_task_shade` SET `parent_app_type` = `app_type`;

ALTER TABLE `schedule_job_job` ADD  `parent_app_type` int(11) NOT NULL DEFAULT '0' COMMENT '父任务的appType';

UPDATE `schedule_job_job` SET `parent_app_type` = `app_type`;

