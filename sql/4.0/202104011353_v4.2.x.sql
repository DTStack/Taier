
-- 逻辑数据源新增project_id、schema_name字段，联合索引加上schema_name
ALTER TABLE `lineage_data_source`
ADD COLUMN `project_id`  bigint(20) NULL COMMENT '项目id' AFTER `source_name`,
ADD COLUMN `schema_name`  varchar(64) NULL COMMENT 'schema或数据库名称' AFTER `project_id`,
ADD COLUMN `source_id`  bigint(20) NULL COMMENT '平台数据源id' AFTER `schema_name`,
DROP INDEX `uni_tenant_source_key` ,
ADD UNIQUE INDEX `uni_tenant_source_key` (`dt_uic_tenant_id`, `source_key`, `app_type`, `source_name`, `schema_name`) USING BTREE;


CREATE TABLE `schedule_sql_text_temp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(32) NOT NULL COMMENT '临时运行job的job_id',
  `sql_text` longtext NOT NULL COMMENT '临时运行任务的sql文本内容',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='临时任务sql_text关联表';


-- 字段血缘表修改result_table_key字段comment
ALTER TABLE `lineage_column_column`
MODIFY COLUMN `result_table_key` varchar(32)  NOT NULL COMMENT '输出表表物理定位码' AFTER `result_table_id`;
