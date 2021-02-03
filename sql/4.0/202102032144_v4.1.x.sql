ALTER TABLE `lineage_data_source`
DROP INDEX `uni_tenant_source_key` ,
ADD UNIQUE INDEX `uni_tenant_source_key` (`dt_uic_tenant_id`, `source_key`, `app_type`, `source_name`) USING BTREE ;