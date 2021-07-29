-- dsc_info添加索引
ALTER TABLE dsc_info ADD INDEX MODIFY_TIME(gmt_modified);

-- dsc_auth_ref 添加索引
ALTER TABLE dsc_auth_ref ADD INDEX AUTH_DATA(data_info_id);

-- dsc_import_ref添加索引
ALTER TABLE dsc_import_ref ADD INDEX DATA_IMPORT(data_info_id);
ALTER TABLE dsc_import_ref ADD INDEX PROJECT_INDEX (project_id,app_type);