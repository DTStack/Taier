DROP TABLE IF EXISTS `rdos_plugin_info`;
CREATE TABLE `rdos_plugin_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `plugin_key` varchar(255) NOT NULL COMMENT '插件配置信息md5值',
  `plugin_info` text NOT NULL COMMENT '插件信息',
  `type` tinyint(2) NOT NULL COMMENT '类型 0:默认插件, 1:动态插件(暂时数据库只存动态插件)',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_plugin_id` (`plugin_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;