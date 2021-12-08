-- 删除数据服务授权
DELETE FROM dsc_app_mapping WHERE data_type = 'Kylin' and app_type =3;

-- 修改data_type的Kylin为Kylin URL
update dsc_type set data_type = 'Kylin URL' where data_type = 'Kylin';

-- 修改data_type的Kylin为Kylin URL
UPDATE dsc_app_mapping set data_type = 'Kylin URL' ,data_version = '3.x' where data_type = 'Kylin';

-- 修改URL格式
UPDATE dsc_form_field set tooltip = '访问Kylin的认证地址，格式为：http://host:7000' WHERE type_version = 'Kylin' and  label = 'RESTful URL';

-- 修改type_version的Kylin为Kylin URL
update dsc_form_field set type_version = 'Kylin URL-3.x' where type_version = 'Kylin';

-- 修改之前Kylin数据源为Kylin URL
update dsc_info set data_type = 'Kylin URL' , data_version = '3.x' where data_type = 'Kylin';

INSERT INTO `dsc_version` ( `data_type`, `data_version`, `sorted`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`) VALUES ( 'Kylin URL', '3.x', 0, 0, '2021-07-06 14:49:27', '2021-07-06 14:49:42', 0, 0);

-- Kylin JDBC 数据源
INSERT INTO `dsc_type` (`data_type`, `data_classify_id`, `weight`, `img_url`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`, `sorted`, `invisible`) VALUES ('Kylin JDBC', 7, 0, 'Kylin.png', 0, '2021-07-06 12:22:10', '2021-07-06 15:49:09', 0, 0, 1300, 0);

-- Kylin JDBC 数据源版本
INSERT INTO `dsc_version` ( `data_type`, `data_version`, `sorted`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`) VALUES ( 'Kylin JDBC', '3.x', 0, 0, '2021-07-06 14:49:27', '2021-07-06 14:49:42', 0, 0);

-- Kylin JDBC 授权给数据服务
INSERT INTO `dsc_app_mapping` ( `app_type`, `data_type`, `data_version`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`) VALUES ( 3, 'Kylin JDBC', '3.x', 0, '2021-07-06 13:49:00', '2021-07-06 13:49:00', 0, 0);

-- Kylin JDBC 表单
INSERT INTO `dsc_form_field` ( `name`, `label`, `widget`, `required`, `invisible`, `default_value`, `place_hold`, `request_api`, `is_link`, `valid_info`, `tooltip`, `style`, `regex`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`, `type_version`, `options`) VALUES ( 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:kylin://host:7070/project_name', NULL, '/jdbc:kylin:\\/\\/(.)+/', 0, '2021-07-06 09:35:57', '2021-07-06 16:07:17', 0, 0, 'Kylin JDBC-3.x', '');
INSERT INTO `dsc_form_field` ( `name`, `label`, `widget`, `required`, `invisible`, `default_value`, `place_hold`, `request_api`, `is_link`, `valid_info`, `tooltip`, `style`, `regex`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`, `type_version`, `options`) VALUES ( 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 0, '2021-07-06 09:35:57', '2021-07-06 10:08:08', 0, 0, 'Kylin JDBC-3.x', '');
INSERT INTO `dsc_form_field` ( `name`, `label`, `widget`, `required`, `invisible`, `default_value`, `place_hold`, `request_api`, `is_link`, `valid_info`, `tooltip`, `style`, `regex`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`, `type_version`, `options`) VALUES ( 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 0, '2021-07-06 09:35:57', '2021-07-06 10:08:12', 0, 0, 'Kylin JDBC-3.x', '');


