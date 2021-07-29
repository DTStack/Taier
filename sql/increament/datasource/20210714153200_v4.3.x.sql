-- 修改SQLServer_2017_LATER为SQLServer JDBC
UPDATE dsc_info set data_type  = 'SQLServer JDBC' where data_type = 'SQLServer_2017_LATER';

-- sqlserver 数据源
INSERT INTO `dsc_type` (`data_type`, `data_classify_id`, `weight`, `img_url`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`, `sorted`, `invisible`) VALUES ('SQLServer JDBC', 3, 0, 'SQLServer.png', 0, '2021-07-06 12:22:10', '2021-07-06 15:49:09', 0, 0, 1200, 0);

-- sqlserver授权给实时
INSERT INTO `dsc_app_mapping` ( `app_type`, `data_type`, `data_version`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`) VALUES (7, 'SQLServer JDBC', '', 0, '2021-07-06 13:49:00', '2021-07-06 13:49:00', 0, 0);

-- sqlserver 表单
INSERT INTO `dsc_form_field` ( `name`, `label`, `widget`, `required`, `invisible`, `default_value`, `place_hold`, `request_api`, `is_link`, `valid_info`, `tooltip`, `style`, `regex`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`, `type_version`, `options`) VALUES ( 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:sqlserver://localhost:1433;DatabaseName=dbName', NULL, '/jdbc:sqlserver:\\/\\/(.)+/', 0, '2021-07-06 09:35:57', '2021-07-06 16:07:17', 0, 0, 'SQLServer JDBC', '');
INSERT INTO `dsc_form_field` ( `name`, `label`, `widget`, `required`, `invisible`, `default_value`, `place_hold`, `request_api`, `is_link`, `valid_info`, `tooltip`, `style`, `regex`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`, `type_version`, `options`) VALUES ( 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 0, '2021-07-06 09:35:57', '2021-07-06 10:08:08', 0, 0, 'SQLServer JDBC', '');
INSERT INTO `dsc_form_field` ( `name`, `label`, `widget`, `required`, `invisible`, `default_value`, `place_hold`, `request_api`, `is_link`, `valid_info`, `tooltip`, `style`, `regex`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`, `type_version`, `options`) VALUES ( 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 0, '2021-07-06 09:35:57', '2021-07-06 10:08:12', 0, 0, 'SQLServer JDBC', '');


