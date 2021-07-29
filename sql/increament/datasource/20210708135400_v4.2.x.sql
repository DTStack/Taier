CREATE TABLE `lock_table` (
                              `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `resource` varchar(128) NOT NULL COMMENT '标识资源',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                              `owner` varchar(128) DEFAULT NULL COMMENT '拥有者',
                              `state` int(4) NOT NULL DEFAULT '1' COMMENT '锁次数',
                              `version` int(4) NOT NULL COMMENT '版本',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `unq_resource` (`resource`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;