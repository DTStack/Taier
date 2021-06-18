CREATE TABLE `console_component_user` (
                                          `id` bigint(11) NOT NULL AUTO_INCREMENT,
                                          `cluster_id` int(11) NOT NULL COMMENT '集群id',
                                          `component_type_code` tinyint(1) NOT NULL COMMENT '组件类型',
                                          `label` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签',
                                          `labelIp` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ip',
                                          `user_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
                                          `password` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
                                          `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                          `is_default` tinyint(1) NOT NULL DEFAULT '1' COMMENT '默认标签',
                                          PRIMARY KEY (`id`),
                                          KEY `CLUSTER_COMPONENT_INDEX` (`cluster_id`,`component_type_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;