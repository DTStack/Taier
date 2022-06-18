CREATE TABLE `task_dirty_data_manage`
(
    `id`                      int(11) NOT NULL AUTO_INCREMENT,
    `task_id`                 int(11) NOT NULL COMMENT '任务id',
    `output_type`             varchar(25) NOT NULL COMMENT '输出类型1.log2.jdbc',
    `max_rows`                int(11) NOT NULL COMMENT '脏数据最大值',
    `max_collect_failed_rows` int(11) NOT NULL COMMENT '失败条数',
    `link_info`               text        NOT NULL COMMENT '连接信息json',
    `log_print_interval`      int(11) NOT NULL DEFAULT '0' COMMENT '日志打印频率',
    `tenant_id`               int(11) NOT NULL COMMENT '租户id',
    `gmt_create`              datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`            datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`              tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `index_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
