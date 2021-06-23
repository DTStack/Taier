-- 清空血缘相关的所有表
alter table lineage_data_source RENAME TO lineage_data_source_deleted;
alter table lineage_real_data_source RENAME TO lineage_real_data_source_deleted;
alter table lineage_data_set_info RENAME TO lineage_data_set_info_deleted;
alter table lineage_table_table RENAME TO lineage_table_table_deleted;
alter table lineage_table_table_unique_key_ref RENAME TO lineage_table_table_unique_key_ref_deleted;
alter table lineage_column_column RENAME TO lineage_column_column_deleted;
alter table lineage_column_column_unique_key_ref RENAME TO lineage_column_column_unique_key_ref_deleted;

-- 表信息表。
create table if not exists lineage_data_set_info(
    id int(11) NOT NULL AUTO_INCREMENT,
    dt_uic_tenant_id int(11) NOT NULL COMMENT '租户id',
    app_type smallint(4) NOT NULL COMMENT '应用类型',
    data_info_id int(11) NOT NULL COMMENT '数据源中心id',
    source_name VARCHAR(55) NOT NULL COMMENT '数据源名称',
    source_type SMALLINT(4) NOT NULL COMMENt '数据源类型',
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
create table if not exists lineage_table_table(
    id int(11) NOT NULL AUTO_INCREMENT,
    app_type smallint(3) NOT NULL COMMENT '应用类型',
    dt_uic_tenant_id int(11) NOT NULL COMMENT '租户id',
    input_table_key varchar(155) NOT NULL COMMENT '输入表表物理定位码',
    input_table_id int(11) NOT NULL COMMENT '输入表id lineage_real_data_source表的id',
    result_table_key varchar(155) NOT NULL COMMENT '输出表表物理定位码',
    result_table_id int(11) NOT NULL COMMENT '输出表id lineage_real_data_source表的id',
    table_lineage_key VARCHAR(30) NOT NULL COMMENT '表血缘定位码，根据输入表和输出表定位码计算出',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    lineage_source smallint(3) NOT NULL DEFAULT '0' COMMENT '血缘来源：0-sql解析；1-手动维护；2-json解析',
    PRIMARY KEY (id),
    UNIQUE KEY uni_table_lineage_key (table_lineage_key)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 表血缘与应用关联表
create table if not exists lineage_table_table_unique_key_ref(
    id int(11) NOT NULL AUTO_INCREMENT,
    app_type smallint(4) NOT NULL COMMENT '应用类型',
    unique_key varchar(32) NOT NULL COMMENT '血缘批次码，离线中通常为taskId',
    lineage_table_table_id int(11) NOT NULL COMMENT 'lineage_table_table表id',
    version_id int(11) NULL DEFAULT 0 COMMENT '任务提交版本号',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uni_appType_tableTableId_uniqueKey (app_type,lineage_table_table_id,unique_key)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 字段级血缘存储方案
create table if not exists lineage_column_column(
    id int(11) NOT NULL AUTO_INCREMENT,
    app_type smallint(3) NOT NULL COMMENT '应用类型',
    dt_uic_tenant_id int(11) NOT NULL COMMENT '租户id',
    input_table_id int(11) NOT NULL COMMENT '输入表id',
    input_table_key varchar(155) NOT NULL COMMENT '输入表表物理定位码',
    input_column_name VARCHAR(55) NOT NULL COMMENT '输入字段名称',
    result_table_id int(11) NOT NULL COMMENT '输出表id',
    result_table_key varchar(155) NOT NULL COMMENT '输出表表物理定位码',
    result_column_name VARCHAR(55) NOT NULL COMMENT '输出字段名称',
    column_lineage_key VARCHAR(60) NOT NULL COMMENT '字段级血缘定位码，根据输入字段和输出字段定位码计算出',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    lineage_source smallint(3) NOT NULL DEFAULT '0' COMMENT '血缘来源：0-sql解析；1-手动维护；2-json解析',
    PRIMARY KEY (id),
    UNIQUE KEY uni_column_lineage_key (column_lineage_key)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 字段血缘与应用关联表
create table if not exists lineage_column_column_unique_key_ref(
    id int(11) NOT NULL AUTO_INCREMENT,
    app_type smallint(4) NOT NULL COMMENT '应用类型',
    unique_key varchar(32) NOT NULL COMMENT '血缘批次码，离线中通常为taskId',
    lineage_column_column_id int(11) NOT NULL COMMENT 'lineage_column_column表id',
    version_id  int(11) NULL DEFAULT 0 COMMENT '任务提交版本号',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uni_appType_columnColumnId_uniqueKey (app_type,lineage_column_column_id,unique_key)
    )ENGINE=InnoDB DEFAULT CHARSET=utf8;