CREATE TABLE `rdos_batch_task_shade`
(
    `id`                      int(11)      NOT NULL AUTO_INCREMENT,
    `tenant_id`               int(11)      NOT NULL DEFAULT '-1' COMMENT '租户id',
    `project_id`              int(11)      NOT NULL DEFAULT '-1' COMMENT '项目id',
    `dtuic_tenant_id`         int(11)      NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`                int(11)      NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `node_pid`                int(11)      NOT NULL COMMENT '父文件夹id',
    `name`                    varchar(256) NOT NULL COMMENT '任务名称',
    `task_type`               tinyint(1)   NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL',
    `engine_type`             tinyint(1)   NOT NULL COMMENT '执行引擎类型 0:flink, 1:spark, 2:datax, 3:learning, 4:shell, 5:python2, 6:dtyarnshell, 7:python3, 8:hadoop, 9:carbon, 10:postgresql, 11:kylin, 12:hive',
    `compute_type`            tinyint(1)   NOT NULL COMMENT '计算类型 0实时，1 离线',
    `sql_text`                LONGTEXT     NOT NULL COMMENT 'sql 文本',
    `task_params`             text         NOT NULL COMMENT '任务参数',
    `task_id`                 int(11)      NOT NULL COMMENT '任务id',
    `schedule_conf`           varchar(512) NOT NULL COMMENT '调度配置 json格式',
    `period_type`             tinyint(2) COMMENT '周期类型',
    `schedule_status`         tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0未开始,1正常调度,2暂停',
    `project_schedule_status` tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常, 1:停止',
    `submit_status`           tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0未提交,1已提交',
    `gmt_create`              datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`            datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modify_user_id`          int(11)      NOT NULL COMMENT '最后修改task的用户',
    `create_user_id`          int(11)      NOT NULL COMMENT '新建task的用户',
    `owner_user_id`           int(11)      NOT NULL COMMENT '负责人id',
    `version_id`              int(11)      NOT NULL DEFAULT '0' COMMENT 'task版本',
    `is_deleted`              tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    `task_desc`               varchar(256) NOT NULL,
    `main_class`              varchar(256) NOT NULL,
    `exe_args`                text                  DEFAULT NULL,
    `flow_id`                 INT(11)      NOT NULL DEFAULT '0' COMMENT '工作流id',
    `is_publish_to_produce`   tinyint(1)   NOT NULL DEFAULT '0' COMMENT '是否发布到生产环境：0-否，1-是',
    `extra_info`              text                  DEFAULT NULL COMMENT '存储task运行时所需的额外信息',
    `is_expire`               TINYINT(1)   NOT NULL DEFAULT '0' COMMENT '过期策略：0永不过期 1过期取消',
    PRIMARY KEY (`id`),
    KEY `index_name` (`project_id`, `name`(128)),
    KEY `index_task_id` (`task_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8;

CREATE TABLE `rdos_batch_task_task_shade`
(
    `id`              int(11)    NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)    NOT NULL COMMENT '租户id',
    `project_id`      int(11)    NOT NULL COMMENT '项目id',
    `dtuic_tenant_id` int(11)    NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)    NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `task_id`         int(11)    NOT NULL COMMENT 'batch 任务id',
    `parent_task_id`  int(11)             DEFAULT NULL COMMENT '对应batch任务父节点的id',
    `gmt_create`      datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `index_batch_task_task` (`task_id`, `parent_task_id`, `project_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE `rdos_batch_job`
(
    `id`              int(11)      NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)      NOT NULL COMMENT '租户id',
    `project_id`      int(11)      NOT NULL COMMENT '项目id',
    `dtuic_tenant_id` int(11)      NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)      NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `job_id`          varchar(256) NOT NULL COMMENT '工作任务id',
    `job_key`         varchar(256) NOT NULL DEFAULT '',
    `job_name`        VARCHAR(256) NOT NULL DEFAULT '',
    `task_id`         int(11)      NOT NULL COMMENT '任务id',
    `gmt_create`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `create_user_id`  int(11)      NOT NULL COMMENT '发起操作的用户',
    `is_deleted`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    `type`            tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常调度 1补数据',
    `is_restart`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0：非重启任务, 1：重启任务',
    `business_date`   varchar(256) NOT NULL COMMENT '业务日期 yyyyMMddHHmmss',
    `cyc_time`        varchar(256) NOT NULL COMMENT '调度时间 yyyyMMddHHmmss',
    `dependency_type` tinyint(2)   NOT NULL DEFAULT 0,
    `flow_job_id`     VARCHAR(256)          DEFAULT '0' NOT NULL COMMENT '工作流实例id',
    `period_type`     tinyint(2)            DEFAULT NULL COMMENT '周期类型',
    `status`          tinyint(1)   NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
    `task_type`       tinyint(1)   NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL',
    `fill_id`         int(11)               DEFAULT 0 COMMENT '补数据id，默认为0',
    `exec_start_time` datetime              DEFAULT NULL COMMENT '执行开始时间',
    `exec_end_time`   datetime              DEFAULT NULL COMMENT '执行结束时间',
    `exec_time`       int(11)               DEFAULT '0' COMMENT '执行时间',
    `submit_time`     datetime              DEFAULT NULL COMMENT '提交时间',
    `max_retry_num`   int(10)      NOT NULL DEFAULT '0' COMMENT '最大重试次数',
    `retry_num`       int(10)      NOT NULL DEFAULT '0' COMMENT '执行时，重试的次数',
    `log_info`        varchar(1024)         DEFAULT NULL COMMENT '调度模块中Job的错误信息，通常比较简短',
    `node_address`    varchar(256)          DEFAULT NULL COMMENT '节点地址',
    `version_id`      int(10)               DEFAULT '0' COMMENT '任务运行时候版本号',
    `next_cyc_time`   varchar(256)          DEFAULT NULL COMMENT '下一次调度时间 yyyyMMddHHmmss',
    PRIMARY KEY (`id`),
    KEY `index_task_id` (`task_id`),
    KEY `index_job_id` (`job_id`),
    KEY `index_fill_id` (`fill_id`),
    KEY `index_project_id` (`project_id`),
    UNIQUE KEY `idx_jobKey` (`job_key`(255)),
    KEY `idx_name_type` (`job_name`(128), `type`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8;

CREATE TABLE `rdos_batch_job_job`
(
    `id`              int(11)      NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)      NOT NULL COMMENT '租户id',
    `project_id`      int(11)      NOT NULL COMMENT '项目id',
    `dtuic_tenant_id` int(11)      NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)      NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `job_key`         VARCHAR(256) NOT NULL COMMENT 'batch 任务key',
    `parent_job_key`  VARCHAR(256)          DEFAULT NULL COMMENT '对应batch任务父节点的key',
    `gmt_create`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_job_parentJobKey` (`job_key`(255), `parent_job_key`(255))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE `rdos_batch_fill_data_job`
(
    `id`              int(11)     NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)     NOT NULL COMMENT '租户id',
    `project_id`      int(11)     NOT NULL COMMENT '项目id',
    `dtuic_tenant_id` int(11)     NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)     NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `job_name`        VARCHAR(64) NOT NULL DEFAULT '',
    `run_day`         VARCHAR(64) NOT NULL COMMENT '补数据运行日期yyyy-MM-dd',
    `from_day`        VARCHAR(64) COMMENT '补数据开始业务日期yyyy-MM-dd',
    `to_day`          VARCHAR(64) COMMENT '补数据结束业务日期yyyy-MM-dd',
    `gmt_create`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `create_user_id`  int(11)     NOT NULL COMMENT '发起操作的用户',
    `is_deleted`      tinyint(1)  NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `index_task_id` (`tenant_id`, `project_id`, `job_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8;

CREATE TABLE `rdos_batch_job_alarm`
(
    `id`              int(11)    NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)    NOT NULL COMMENT '租户id',
    `project_id`      int(11)    NOT NULL COMMENT '项目id',
    `dtuic_tenant_id` int(11)    NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)    NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `job_id`          int(11)    NOT NULL COMMENT 'batch job id',
    `task_id`         int(11)    NOT NULL COMMENT 'batch task id',
    `task_status`     int(2)     NOT NULL COMMENT '当前任务状态',
    `gmt_create`      datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY `index_task_id` (`task_id`),
    UNIQUE KEY `index_batch_job_alarm_name` (`job_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `rdos_batch_alarm`
(
    `id`              int(11)      NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)      NOT NULL COMMENT '租户id',
    `project_id`      int(11)      NOT NULL COMMENT '项目id',
    `dtuic_tenant_id` int(11)      NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)      NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `name`            varchar(200) NOT NULL COMMENT '告警名称',
    `task_id`         int(11)      NOT NULL COMMENT 'batch 任务id',
    `my_trigger`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0 失败 1完成 2 未完成',
    `uncomplete_time` VARCHAR(20)           DEFAULT NULL COMMENT 'batch 任务 未完成超时的时间设置,HH:mm',
    `status`          tinyint(1)   NOT NULL default 0 comment '0 正常 1关闭 2删除',
    `create_user_id`  int(11)      NOT NULL COMMENT '创建的用户',
    `gmt_create`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    `sender_type`     int(4)       NOT NULL DEFAULT '0' COMMENT '发送的客户端类型 0x001: 邮件 0x010: 短信',
    `webhook`         varchar(256) NOT NULL DEFAULT '' COMMENT '钉钉告警-自定义机器人的webhook',
    `is_task_holder`  tinyint(1)   NOT NULL DEFAULT '1' COMMENT '1有任务负责人 0无任务负责人',
    `receivers`       text         NULL COMMENT '告警接收人详细信息',
    PRIMARY KEY (`id`),
    KEY `index_task_id` (`task_id`),
    KEY `index_batch_alarm_name` (`project_id`, `name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `rdos_batch_alarm_record`
(
    `id`              int(11)      NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)      NOT NULL COMMENT '租户id',
    `project_id`      int(11)      NOT NULL COMMENT '项目id',
    `dtuic_tenant_id` int(11)      NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)      NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `alarm_id`        int(11)      NOT NULL COMMENT '告警id',
    `cyc_time`        VARCHAR(32) COMMENT '批处理调度的时间',
    `alarm_content`   varchar(512) NOT NULL COMMENT '告警内容',
    `trigger_type`    tinyint(1)   NOT NULL DEFAULT '0' COMMENT '触发方式 0:BEFORE 1:AFTER 2:INSTEAD_OF',
    `gmt_create`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY `index_alarm_id` (`alarm_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `rdos_batch_alarm_record_user`
(
    `id`              int(11)    NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)    NOT NULL COMMENT '租户id',
    `project_id`      int(11)    NOT NULL COMMENT '项目id',
    `dtuic_tenant_id` int(11)    NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)    NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `alarm_record_id` int(11)    NOT NULL COMMENT '告警id',
    `user_id`         int(11)    NOT NULL COMMENT '告警用户id',
    `gmt_create`      datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY `user_id` (`user_id`),
    KEY `alarm_record_id` (`alarm_record_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `rdos_job_graph_trigger`
(
    `id`           int(11)    NOT NULL AUTO_INCREMENT,
    `trigger_type` tinyint(3) NOT NULL COMMENT '0:正常调度 1补数据',
    `trigger_time` datetime   NOT NULL,
    `gmt_create`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`   int(10)    NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `index_trigger_time` (`trigger_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE `rdos_notify`
(
    `id`              int(11)      NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)      NOT NULL DEFAULT '0' COMMENT '租户id',
    `project_id`      int(11)      NOT NULL DEFAULT '0' COMMENT '项目id',
    `dtuic_tenant_id` int(11)      NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)      NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `biz_type`        tinyint(1)   NOT NULL COMMENT '业务类型，1：实时，2：离线',
    `relation_id`     int(11)      NOT NULL DEFAULT '0' COMMENT '关联id',
    `name`            varchar(256)          DEFAULT '' COMMENT '通知名称',
    `trigger_type`    tinyint(1)   NOT NULL DEFAULT '0' COMMENT '触发类型 0:failed, 1:finished, 2:unfinished 3:canceled, 4:timing-uncompleted 5:timing-exec-over',
    `webhook`         varchar(256) NOT NULL DEFAULT '' COMMENT '钉钉告警-自定义机器人的webhook',
    `uncomplete_time` VARCHAR(20)           DEFAULT NULL COMMENT 'batch 任务 未完成超时的时间设置,HH:mm',
    `send_way`        varchar(128) NOT NULL DEFAULT '' COMMENT '通知方式，从右到左如果不为0即选中（索引位从0开始，第1位：邮件，第2位: 短信，第3位: 微信，第4位: 钉钉）',
    `start_time`      varchar(256)          DEFAULT '' COMMENT '允许通知的开始时间，如5：00，早上5点',
    `end_time`        varchar(256)          DEFAULT '' COMMENT '允许通知的结束时间，如22：00，不接受告警',
    `status`          tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0：正常，1：停止，2 停止访问',
    `create_user_id`  int(11)      NOT NULL COMMENT '创建的用户',
    `gmt_create`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_biz_type_relation_id` (`biz_type`, `relation_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='通知表';

CREATE TABLE `rdos_notify_record`
(
    `id`              int(11)    NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)    NOT NULL DEFAULT '0' COMMENT '租户id',
    `project_id`      int(11)    NOT NULL DEFAULT '0' COMMENT '项目id',
    `dtuic_tenant_id` int(11)    NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)    NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `notify_id`       int(11)    NOT NULL DEFAULT '0' COMMENT '通知id',
    `content_id`      int(11)    NOT NULL DEFAULT '0' COMMENT '内容文本id',
    `cyc_time`        VARCHAR(32) COMMENT '批处理调度的时间',
    `status`          tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 0:unread, 1:read, 2:unaccess',
    `gmt_create`      datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY `notify_id` (`notify_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='消息记录';

CREATE TABLE `rdos_notify_user`
(
    `id`              int(11)    NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)    NOT NULL DEFAULT '0' COMMENT '租户id',
    `project_id`      int(11)    NOT NULL DEFAULT '0' COMMENT '项目id',
    `dtuic_tenant_id` int(11)    NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)    NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `notify_id`       int(11)    NOT NULL COMMENT '通知id',
    `user_id`         int(11)    NOT NULL COMMENT '接收人id',
    `gmt_create`      datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_notify_id_user_id` (`notify_id`, `user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='通知与接收人关系表';

CREATE TABLE `rdos_notify_alarm`
(
    `id`              int(11)    NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)    NOT NULL DEFAULT '0' COMMENT '租户id',
    `project_id`      int(11)    NOT NULL DEFAULT '0' COMMENT '项目id',
    `dtuic_tenant_id` int(11)    NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)    NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `biz_type`        tinyint(1) NOT NULL COMMENT '业务类型，1：实时，2：离线',
    `notify_id`       int(11)    NOT NULL COMMENT '通知id',
    `alarm_id`        int(11)    NOT NULL COMMENT '告警id',
    `gmt_create`      datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_biz_type_notify_id_alarm_id` (`biz_type`, `notify_id`, `alarm_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='通知记录表';
