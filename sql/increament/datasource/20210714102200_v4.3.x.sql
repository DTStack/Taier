-- 去掉kafka的form_field的openKerberos
delete from dsc_form_field where type_version like "kafka%" and `name` = "openKerberos";


-- GreenPlum给实时授权
INSERT INTO `dsc_app_mapping` (`app_type`, `data_type`, `data_version`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`)
VALUES
	(7, 'Greenplum', '', 0, '2021-07-12 14:03:21', '2021-07-12 14:03:21', 0, 0);

