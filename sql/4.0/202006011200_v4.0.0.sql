ALTER TABLE console_component RENAME TO console_component_bak;
ALTER TABLE console_kerberos RENAME TO console_kerberos_bak;
ALTER TABLE console_engine RENAME TO console_engine_bak;
ALTER TABLE console_engine_tenant RENAME TO console_engine_tenant_bak;
ALTER TABLE console_cluster RENAME TO console_cluster_bak;

ALTER TABLE schedule_job
    ADD INDEX idx_gmt_modified (`gmt_modified`) COMMENT '修改时间';
ALTER TABLE schedule_job
    ADD INDEX idx_cyctime (`cyc_time`) COMMENT '执行时间';
ALTER TABLE console_engine
    ADD UNIQUE INDEX `index_cluster_engineType` (`cluster_id`, `engine_type`);

CREATE TABLE `console_engine`
(
    `id`           int(11)     NOT NULL AUTO_INCREMENT,
    `cluster_id`   int(11)     NOT NULL COMMENT '集群id',
    `engine_name`  varchar(24) NOT NULL COMMENT '引擎名称',
    `engine_type`  tinyint(4)  NOT NULL COMMENT '引擎类型',
    `total_node`   int(11)     NOT NULL COMMENT '节点数',
    `total_memory` int(11)     NOT NULL COMMENT '总内存',
    `total_core`   int(11)     NOT NULL COMMENT '总核数',
    `sync_type`    tinyint(1)  NULL COMMENT '获取元数据组件类型',
    `gmt_create`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`   tinyint(1)  NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `console_component`
(
    `id`                  int(11)     NOT NULL AUTO_INCREMENT,
    `engine_id`           int(11)     NOT NULL COMMENT '引擎id',
    `component_name`      varchar(24) NOT NULL COMMENT '组件名称',
    `component_type_code` tinyint(1)  NOT NULL COMMENT '组件类型',
    `component_config`    text        NOT NULL COMMENT '组件配置',
    `gmt_create`          datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`        datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`          tinyint(1)  NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    `hadoop_version`      varchar(25)          DEFAULT '' COMMENT '组件hadoop版本',
    `upload_file_name`    varchar(50)          DEFAULT '' COMMENT '上传文件zip名称',
    `component_template`  text COMMENT '前端展示模版json',
    `kerberos_file_name`  varchar(50)          DEFAULT '' COMMENT '上传kerberos文件zip名称',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `index_component` (`engine_id`, `component_type_code`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8;


CREATE TABLE `console_engine_tenant`
(
    `id`           int(11)    NOT NULL AUTO_INCREMENT,
    `tenant_id`    int(11)    NOT NULL COMMENT '租户id',
    `engine_id`    int(11)    NOT NULL COMMENT '引擎id',
    `queue_id`     int(11)    NULL COMMENT '队列id',
    `gmt_create`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



CREATE TABLE `console_kerberos`
(
    `id`             bigint(20)   NOT NULL AUTO_INCREMENT,
    `cluster_id`     int(11)      NOT NULL COMMENT '集群id',
    `open_kerberos`  tinyint(1)   NOT NULL COMMENT '是否开启kerberos配置',
    `name`           varchar(100) NOT NULL COMMENT 'kerberos文件名称',
    `remote_path`    varchar(200) NOT NULL COMMENT 'sftp存储路径',
    `principal`      varchar(50)  NOT NULL COMMENT 'principal',
    `gmt_create`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`     tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    `krb_name`       varchar(26)           DEFAULT NULL COMMENT 'krb5_conf名称',
    `component_type` int(11)               DEFAULT NULL COMMENT '组件类型',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8;



CREATE TABLE `console_cluster`
(
    `id`             int(11)     NOT NULL AUTO_INCREMENT,
    `cluster_name`   varchar(24) NOT NULL COMMENT '集群名称',
    `hadoop_version` varchar(24) NOT NULL COMMENT 'hadoop版本',
    `gmt_create`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`     tinyint(1)  NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx` (`cluster_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;