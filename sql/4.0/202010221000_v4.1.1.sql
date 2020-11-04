-- 物理数据源表
create table lineage_real_data_source(
    id int(11) NOT NULL AUTO_INCREMENT,
    source_name VARCHAR(155) NOT NULL COMMENT '数据源名称',
    source_key VARCHAR(155) NOT NULL COMMENT '数据源定位码，不同数据源类型计算方式不同。',
    source_type SMALLINT(4) NOT NULL COMMENt '数据源类型',
    data_jason JSON NOT NULL COMMENT '数据源配置json',
    kerberos_conf JSON NOT NULL COMMENT 'kerberos配置',
    open_kerberos tinyint NOT NULL default 0 COMMENT '0：未开启kerberos；1：开启kerberos',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uni_source_key (source_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 逻辑数据源表
create table lineage_data_source(
    id int(11) NOT NULL AUTO_INCREMENT,
    dt_uic_tenant_id int(11) NOT NULL COMMENT '租户id',
    real_source_id int(11) NOT NULL COMMENT '真实数据源id',
    source_key VARCHAR(155) NOT NULL COMMENT '数据源定位码，不同数据源类型计算方式不同。',
    source_name VARCHAR(55) NOT NULL COMMENT '数据源名称',
    app_type smallint(4) NOT NULL COMMENT '应用类型',
    source_type smallint(4) NOT NULL COMMENT '数据源类型',
    data_jason JSON NOT NULL COMMENT '数据源配置json',
    kerberos_conf JSON NOT NULL COMMENT 'kerberos配置',
    open_kerberos tinyint NOT NULL default 0 COMMENT '0：未开启kerberos；1：开启kerberos',
    app_source_id int(11) NOT NULL default -1 COMMENT '应用内的sourceId',
    inner_source tinyint(1) NOT NULL default 0 COMMENT '是否内部数据源；0 不是；1 内部数据源。内部数据源有ComponentId',
    component_id int(11) NOT NULL default -1 COMMENT '数据源对应的组件id',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uni_tenant_source_key (tenant_id,source_key)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 表信息表。表可能并不能关联上data source。
create table lineage_data_set_info(
    id int(11) NOT NULL AUTO_INCREMENT,
    dt_uic_tenant_id int(11) NOT NULL COMMENT '租户id',
    app_type smallint(4) NOT NULL COMMENT '应用类型',
    source_id int(11) NOT NULL COMMENT 'lineage_data_source中的id',
    real_source_id int(11) NOT NULL COMMENT '真实数据源id',
    source_name VARCHAR(55) NOT NULL COMMENT '数据源名称',
    source_type SMALLINT(4) NOT NULL COMMENt '数据源类型',
    source_key VARCHAR(155) NOT NULL COMMENT '数据源定位码，不同数据源类型计算方式不同。',
    set_type smallint(4) NOT NULL default 0 COMMENT '数据集类型，0 表 ，1 文件',
    db_name VARCHAR(55) NOT NULL COMMENT '一般数据集类型为表，该字段为数据库名称;当数据集类型为文件时，该字段可以取文件名，或者其他定义',
    schema_name VARCHAR(55) NOT NULL COMMENT '一般情况下，db_name=schema_name，SQLserver中，表定义形式为db.schema.table',
    table_name VARCHAR(55) NOT NULL COMMENT '一般数据集类型为表，该字段为表名称；当数据集类型为文件时，该字段可以由文件描述的数据集模型名定义',
    table_key VARCHAR(155) NOT NULL COMMENT '表定位码，根据sourceId、数据库名、表名确定下来的表定位码',
    is_manual smallint(3) NOT NULL DEFAULT '0' COMMENT '0正常 1手动维护',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uni_table_key (table_key)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 表级血缘记录表
create table lineage_table_table(
    id int(11) NOT NULL AUTO_INCREMENT,
    app_type smallint(3) NOT NULL COMMENT '应用类型',
    dt_uic_tenant_id int(11) NOT NULL COMMENT '租户id',
    input_table_key varchar(32) NOT NULL COMMENT '输入表表物理定位码',
    input_table_id int(11) NOT NULL COMMENT '输入表id lineage_real_data_source表的id',
    result_table_key varchar(32) NOT NULL COMMENT '输出表表物理定位码',
    result_table_id int(11) NOT NULL COMMENT '输出表id lineage_real_data_source表的id',
    table_lineage_key VARCHAR(30) NOT NULL COMMENT '表血缘定位码，根据输入表和输出表定位码计算出',
    unique_key VARCHAR(32) NOT NULL COMMENT '批次血缘唯一码，比如离线中使用taskId作为血缘批次控制',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    lineage_source smallint(3) NOT NULL DEFAULT '0' COMMENT '血缘来源：0-sql解析；1-手动维护；2-json解析',
    PRIMARY KEY (id),
    UNIQUE KEY uni_table_lineage_key (table_lineage_key)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 表血缘与应用关联表
create table lineage_table_table_unique_key_ref(
    id int(11) NOT NULL AUTO_INCREMENT,
    app_type smallint(4) NOT NULL COMMENT '应用类型',
    uniqueKey varchar(32) NOT NULL COMMENT '血缘批次码，离线中通常为taskId',
    lineage_table_table_id int(11) NOT NULL COMMENT 'lineage_table_table表id',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 字段级血缘存储方案
create table lineage_column_column(
    id int(11) NOT NULL AUTO_INCREMENT,
    app_type smallint(3) NOT NULL COMMENT '应用类型',
    dt_uic_tenant_id int(11) NOT NULL COMMENT '租户id',
    input_table_id int(11) NOT NULL COMMENT '输入表id',
    input_table_key varchar(32) NOT NULL COMMENT '输入表表物理定位码',
    input_column_name VARCHAR(55) NOT NULL COMMENT '输入字段名称',
    result_table_id int(11) NOT NULL COMMENT '输出表id',
    result_table_key varchar(32) NOT NULL COMMENT '输入表表物理定位码',
    result_column_name VARCHAR(55) NOT NULL COMMENT '输出字段名称',
    column_lineage_key VARCHAR(60) NOT NULL COMMENT '字段级血缘定位码，根据输入字段和输出字段定位码计算出',
    unique_key VARCHAR(32) NOT NULL COMMENT '批次血缘唯一码，比如离线中使用taskId作为血缘批次控制',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    lineage_source smallint(3) NOT NULL DEFAULT '0' COMMENT '血缘来源：0-sql解析；1-手动维护；2-json解析',
    PRIMARY KEY (id),
    UNIQUE KEY uni_column_lineage_key (column_lineage_key)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 字段血缘与应用关联表
create table lineage_column_column_unique_key_ref(
    id int(11) NOT NULL AUTO_INCREMENT,
    app_type smallint(4) NOT NULL COMMENT '应用类型',
    uniqueKey varchar(32) NOT NULL COMMENT '血缘批次码，离线中通常为taskId',
    lineage_column_column_id int(11) NOT NULL COMMENT 'lineage_column_column表id',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;