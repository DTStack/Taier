-- 字典表添加analyticdbForPg配置
INSERT INTO `schedule_dict` ( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ( 'typename_mapping', 'adb-postgresql', '-104', NULL, 6, 0, 'LONG', '', 0, '2021-05-18 17:50:23', '2021-05-18 17:50:23', 0);

ALTER TABLE console_engine MODIFY COLUMN engine_name VARCHAR(26);