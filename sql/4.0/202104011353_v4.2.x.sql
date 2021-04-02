
-- 逻辑数据源新增project_id、schema_name字段，联合索引加上schema_name
ALTER TABLE `lineage_data_source`
ADD COLUMN `project_id`  bigint(20) NULL COMMENT '项目id' AFTER `source_name`,
ADD COLUMN `schema_name`  varchar(64) NULL COMMENT 'schema或数据库名称' AFTER `project_id`,
ADD COLUMN `source_id`  bigint(20) NULL COMMENT '平台数据源id' AFTER `schema_name`,
DROP INDEX `uni_tenant_source_key` ,
ADD UNIQUE INDEX `uni_tenant_source_key` (`dt_uic_tenant_id`, `source_key`, `app_type`, `source_name`, `schema_name`) USING BTREE