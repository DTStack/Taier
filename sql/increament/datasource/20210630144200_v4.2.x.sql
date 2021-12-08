-- dsc_info表添加schemaName字段
ALTER TABLE `dsc_info` ADD COLUMN `schema_name` varchar(64) DEFAULT ''
COMMENT '数据源schemaName' AFTER `data_type_code`;


-- 往dsc_type表新增 OpenTSDB类型数据源
INSERT INTO `dsc_type` (`data_type`, `data_classify_id`, `weight`, `img_url`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`, `sorted`, `invisible`)
VALUES
   ('OpenTSDB', 11, 0.0, 'OpenTSDB.png', 0, '2021-07-06 10:37:27', '2021-07-06 10:37:42', 0, 0, 862, 0);


-- 在数据源版本表dsc_version中新增一条数据
INSERT INTO `dsc_version` (`data_type`, `data_version`, `sorted`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`)VALUES
	( 'OpenTSDB', '2.x', 0, 0, '2021-07-06 10:37:27', '2021-07-06 10:37:42', 0, 0);

-- 在数据源和产品映射表dsc_app_mapping表中新增一条数据
INSERT INTO `dsc_app_mapping` (`app_type`, `data_type`, `data_version`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`)VALUES
	( 1, 'OpenTSDB', '2.x', 0, '2021-07-06 10:37:27', '2021-07-06 10:37:42', 0, 0);

-- dsc_form_field表新增几条和OpenTSDB相关的数据
INSERT INTO `dsc_form_field` (`name`, `label`, `widget`, `required`, `invisible`, `default_value`, `place_hold`, `request_api`, `is_link`, `valid_info`, `tooltip`, `style`, `regex`, `type_version`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`)VALUES
	('url', 'URL', 'Input', 1, 0, NULL, 'http://localhost:4242', NULL, 1, '{\"regex\":{\"message\":\"URL格式不符合规则!\"}}', NULL, NULL, '/http:\\/\\/([\\w, .])+:(.)+/', 'OpenTSDB-2.x', 0, '2021-07-06 10:37:27', '2021-07-06 10:37:42', 0, 0);

-- Elasticsearch 5.x、6.x、7.x的集群名称均改为非必填
update `dsc_form_field`	 set `required` = 0 where type_version = 'Elasticsearch-5.x' and  `name` = 'clusterName';
update `dsc_form_field`	 set `required` = 0 where type_version = 'Elasticsearch-6.x' and  `name` = 'clusterName';
update `dsc_form_field`	 set `required` = 0 where type_version = 'Elasticsearch-7.x' and  `name` = 'clusterName';


-- 修改InfluxDB的weight
update `dsc_type` set weight = weight - 10.0 where data_type = 'InfluxDB';


-- solr数据源给api权限
INSERT INTO `dsc_app_mapping` ( `app_type`, `data_type`, `data_version`, `is_deleted`, `gmt_create`, `gmt_modified`, `create_user_id`, `modify_user_id`)
VALUES
( 3, 'Solr', '7.x', 0, '2021-07-07 11:30:00', '2021-07-07 11:30:00', 0, 0);