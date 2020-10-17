CREATE TABLE `console_tenant_resource`
(
  `id`           int(11)    NOT NULL AUTO_INCREMENT,
  `tenant_id`   int(11)     NOT NULL COMMENT '租户id',
  `dt_uic_tenant_id` int(11) NOT NULL COMMENT 'uic租户id',
  `task_type` tinyint(2)   NOT NULL COMMENT '任务类型',
  `engine_type` varchar(256) NOT NULL COMMENT '任务类型名称',
  `resource_limit` 	text NOT NULL COMMENT '资源限制',
  `gmt_create`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted`   int(10)    NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT '租户资源限制表';

ALTER TABLE `console_tenant_resource` ADD UNIQUE `idx_uic_tenantid_tasktype`
USING BTREE (`dt_uic_tenant_id`, `task_type`) comment  `添加uic租户id和任务类型的联合唯一索引` ;