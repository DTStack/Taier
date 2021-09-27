create table schedule_job_operator_record
(
    id                int auto_increment
        primary key,
    job_id            varchar(255)                         not null comment '任务id',
    version           int(10)    default 0                 null comment '版本号',
    gmt_create        datetime   default CURRENT_TIMESTAMP not null comment '新增时间',
    gmt_modified      datetime   default CURRENT_TIMESTAMP not null comment '修改时间',
    operator_expired  datetime   default CURRENT_TIMESTAMP not null comment '操作过期时间',
    is_deleted        tinyint(1) default 0                 not null comment '0正常 1逻辑删除',
    force_cancel_flag tinyint(1) default 0                 not null comment '强制标志 0非强制 1强制',
    operator_type     tinyint(1) default 0                 not null comment '操作类型 0杀死 1重跑 2 补数据',
    node_address      varchar(255)                         null comment '节点地址',
    unique (job_id, operator_type,is_deleted)
)
    charset = utf8;
