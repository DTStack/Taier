-- 校验是否存在备份表
DROP TABLE IF EXISTS rdos_hive_table_action_record_back_20210706;

-- 创建临时表 存储需要剩余的操作记录
alter table rdos_hive_table_action_record rename to rdos_hive_table_action_record_back_20210706;

-- 创建新的rdos_hive_table_action_record表
CREATE TABLE `rdos_hive_table_action_record`(
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `table_id` int(11) NOT NULL COMMENT '表id',
  `action_sql` LONGTEXT NOT NULL COMMENT '操作sql语句',
  `operate` VARCHAR(100) NOT NULL COMMENT '操作类型',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_table_id`(`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
