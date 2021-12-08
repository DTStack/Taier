-- 离线和数据源中心关联表，已废弃
CREATE TABLE IF not EXISTS `rdos_batch_data_source_center` (
    `id` INT (11) NOT NULL AUTO_INCREMENT COMMENT '离线数据源id',
    `project_id` INT (11) DEFAULT NULL COMMENT '项目ID',
    `tenant_id` INT (11) DEFAULT NULL COMMENT '租户ID',
    `create_user_id` INT (11) DEFAULT NULL,
    `modify_user_id` INT (11) DEFAULT NULL,
    `gmt_create` DATETIME DEFAULT NULL,
    `gmt_modified` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT (1) DEFAULT 0,
    `is_default` TINYINT (1) DEFAULT 0,
    `dt_center_source_id` INT (11) NOT NULL COMMENT '数据源中心ID',
    PRIMARY KEY (`id`)
) ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT = '离线和数据源中心关联表';