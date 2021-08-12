
-- 逻辑数据源新增project_id、schema_name字段，联合索引加上schema_name
ALTER TABLE `lineage_data_source`
ADD COLUMN `project_id`  bigint(20) NULL COMMENT '项目id' AFTER `source_name`,
ADD COLUMN `schema_name`  varchar(64) NULL COMMENT 'schema或数据库名称' AFTER `project_id`,
ADD COLUMN `source_id`  bigint(20) NULL COMMENT '平台数据源id' AFTER `schema_name`,
ADD COLUMN `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否是默认数据源，1是，0否' AFTER `component_id`,
DROP INDEX `uni_tenant_source_key` ,
ADD UNIQUE INDEX `uni_tenant_source_key` (`dt_uic_tenant_id`, `source_key`, `app_type`, `source_name`, `schema_name`,`project_id`) USING BTREE;


CREATE TABLE if not exists `schedule_sql_text_temp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(32) NOT NULL COMMENT '临时运行job的job_id',
  `sql_text` longtext NOT NULL COMMENT '临时运行任务的sql文本内容',
  `engine_type` varchar(64) NOT NULL COMMENT 'engine类型',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='临时任务sql_text关联表';

ALTER TABLE `lineage_table_table_unique_key_ref`
ADD COLUMN `version_id`  int(11) NULL DEFAULT 0 COMMENT '任务提交版本号' AFTER `lineage_table_table_id`;

ALTER TABLE `lineage_column_column_unique_key_ref`
ADD COLUMN `version_id`  int(11) NULL DEFAULT 0 COMMENT '任务提交版本号' AFTER `lineage_column_column_id`;


-- 字段血缘表修改result_table_key字段comment
ALTER TABLE `lineage_column_column`
MODIFY COLUMN `result_table_key` varchar(256)  NOT NULL COMMENT '输出表表物理定位码' AFTER `result_table_id`;
