-- 数据源中心支持的产品列表
CREATE TABLE `dsc_app_list` (
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `app_type` int(5) NOT NULL COMMENT '产品type code',
    `app_code` varchar(64) NOT NULL COMMENT '产品唯一编码 如batch、stream',
    `app_name` varchar(64) NOT NULL COMMENT '产品名称 如实时计算等',
    `invisible` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否隐藏 0-不隐藏 1-隐藏',
    `sorted` int(5) NOT NULL DEFAULT 0 COMMENT '产品列表排序字段 默认从0开始',
    `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_user_id` int(11) DEFAULT '0',
    `modify_user_id` int(11) DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`app_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源中心支持的产品列表';

-- 数据源分类表
CREATE TABLE `dsc_classify`(
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `classify_code` varchar(64) NOT NULL COMMENT '类型栏唯一编码',
    `sorted` int(5) NOT NULL DEFAULT 0 COMMENT '类型栏排序字段 默认从0开始',
    `classify_name` varchar(64) NOT NULL COMMENT '类型名称 包含全部和常用栏',
    `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_user_id` int(11) DEFAULT '0',
    `modify_user_id` int(11) DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`classify_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源分类表';

-- 数据源类型信息表
CREATE TABLE `dsc_type`(
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `data_type` varchar(64) NOT NULL COMMENT '数据源类型唯一 如Mysql, Oracle, Hive',
    `data_classify_id` int(11) NOT NULL COMMENT '数据源分类栏主键id',
    `weight` decimal(20, 1) NOT NULL DEFAULT 0.0 COMMENT '数据源权重',
    `img_url` varchar(256) DEFAULT NULL COMMENT '数据源logo图片地址',
    `sorted` int(5) NOT NULL DEFAULT 0 COMMENT '数据源类型排序字段, 默认从0开始',
    `invisible` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否可见',
    `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_user_id` int(11) DEFAULT '0',
    `modify_user_id` int(11) DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`data_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源类型信息表';

-- 数据源版本表
CREATE TABLE `dsc_version` (
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `data_type` varchar(64) NOT NULL COMMENT '数据源类型唯一 如Mysql, Oracle, Hive',
    `data_version` varchar(64) NOT NULL COMMENT '数据源版本 如1.x, 0.9',
    `sorted` int(5) NOT NULL DEFAULT 0 COMMENT '版本排序字段,高版本排序,默认从0开始',
    `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_user_id` int(11) DEFAULT '0',
    `modify_user_id` int(11) DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`data_type`, `data_version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源版本表';

-- 数据源和产品映射表
CREATE TABLE `dsc_app_mapping` (
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `app_type` int(5) NOT NULL COMMENT '产品type code',
    `data_type` varchar(64) NOT NULL COMMENT '数据源类型唯一 如Mysql, Oracle, Hive',
    `data_version` varchar(64) DEFAULT '' COMMENT '数据源版本 如1.x, 0.9',
    `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_user_id` int(11) DEFAULT '0',
    `modify_user_id` int(11) DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`app_type`,`data_type`, `data_version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源和产品映射表';

-- 数据源详细信息表
CREATE TABLE `dsc_info` (
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `data_type` varchar(64) NOT NULL COMMENT '数据源类型唯一 如Mysql, Oracle, Hive',
    `data_version` varchar(64) DEFAULT NULL COMMENT '数据源版本 如1.x, 0.9, 创建下的实例可能会没有版本号',
    `data_name` varchar(128) NOT NULL COMMENT '数据源名称',
    `data_desc` text DEFAULT NULL COMMENT '数据源描述',
    `link_json` text DEFAULT NULL COMMENT '数据源连接信息, 不同数据源展示连接信息不同, 保存为json',
    `data_json` text DEFAULT NULL COMMENT '数据源填写的表单信息, 保存为json, key键要与表单的name相同',
    `status` tinyint(4) NOT NULL COMMENT '连接状态 0-连接失败, 1-正常',
    `is_meta` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否有meta标志 0-否 1-是',
    `tenant_id` int(11) NOT NULL COMMENT '租户主键id **可能不是id 其他唯一凭证',
    `dtuic_tenant_id` bigint(20) unsigned NOT NULL COMMENT 'dtuic的租户id',
    `data_type_code` tinyint(4) NOT NULL COMMENT '数据源类型编码',
    `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_user_id` int(11) DEFAULT '0',
    `modify_user_id` int(11) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源详细信息表';

-- 数据源与授权产品关联中间表
CREATE TABLE `dsc_auth_ref`(
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `data_info_id` int(11) NOT NULL COMMENT '数据源实例主键id',
    `app_type` int(5) NOT NULL COMMENT '产品type code',
    `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_user_id` int(11) DEFAULT '0',
    `modify_user_id` int(11) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源与授权产品关联中间表';

-- 数据源与引入产品关联中间表
CREATE TABLE `dsc_import_ref`
(
    `id`             int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `data_info_id`   int(11)          NOT NULL COMMENT '数据源实例主键id',
    `old_data_info_id`  int(11) DEFAULT -1 COMMENT '各平台数据源id',
    `dtuic_tenant_id` bigint(20) DEFAULT 0 COMMENT 'dtuic的租户id',
    `project_id`     bigint(20) DEFAULT -1 COMMENT '项目id，默认为-1',
    `app_type`       int(5)           NOT NULL COMMENT '产品type code',
    `is_deleted`     tinyint(4)       NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
    `gmt_create`     datetime         DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`   datetime         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_user_id` int(11)          DEFAULT '0',
    `modify_user_id` int(11)          DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='数据源与引入产品关联中间表';

-- 数据源表单属性表
CREATE TABLE `dsc_form_field`(
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `name` varchar(64) NOT NULL COMMENT '表单属性名称，同一模版表单中不重复',
    `label` varchar(64) NOT NULL COMMENT '属性前label名称',
    `widget` varchar(64) NOT NULL COMMENT '属性格式 如Input, Radio等',
    `required` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否必填 0-非必填 1-必填',
    `invisible` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否为隐藏 0-否 1-隐藏',
    `default_value` text DEFAULT NULL COMMENT '表单属性中默认值, 默认为空',
    `place_hold` text DEFAULT NULL COMMENT '输入框placeHold, 默认为空',
    `request_api` varchar(256) DEFAULT NULL COMMENT '请求数据Api接口地址，一般用于关联下拉框类型，如果不需要请求则为空',
    `is_link` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否为数据源需要展示的连接信息字段。0-否; 1-是',
    `valid_info` text DEFAULT NULL COMMENT '校验返回信息文案',
    `tooltip` text DEFAULT NULL COMMENT '输入框后问号的提示信息',
    `style` text DEFAULT NULL COMMENT '前端表单样式参数',
    `regex` text DEFAULT NULL COMMENT '正则校验表达式',
    `type_version` varchar(64) NOT NULL COMMENT '对应数据源版本信息',
    `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_user_id` int(11) DEFAULT '0',
    `modify_user_id` int(11) DEFAULT '0',
    `options` varchar(256) DEFAULT '' COMMENT 'select组件下拉内容',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`name`,`type_version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源表单属性表';

-- dsc_info表data_type_code字段改为非必填默认值为0
ALTER TABLE `dsc_info` CHANGE COLUMN `data_type_code` `data_type_code` tinyint(4) NOT NULL DEFAULT 0 COMMENT '数据源类型编码';