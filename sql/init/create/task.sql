CREATE TABLE `schedule_task_shade`
(
    `id`                      int(11)      NOT NULL AUTO_INCREMENT,
    `tenant_id`               int(11)      NOT NULL DEFAULT '-1' COMMENT '租户id',
    `project_id`              int(11)      NOT NULL DEFAULT '-1' COMMENT '项目id',
    `dtuic_tenant_id`         int(11)      NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`                int(11)      NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `node_pid`                int(11)      DEFAULT NULL COMMENT '父文件夹id',
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
    `task_desc`               varchar(256) NOT NULL             COMMENT '任务描述',
    `main_class`              varchar(256) NOT NULL             COMMENT 'Jar包的入口函数',
    `exe_args`                text                  DEFAULT NULL COMMENT '额外参数',
    `flow_id`                 INT(11)      NOT NULL DEFAULT '0' COMMENT '工作流id',
    `is_publish_to_produce`   tinyint(1)   NOT NULL DEFAULT '0' COMMENT '是否发布到生产环境：0-否，1-是',
    `extra_info`              mediumtext                  DEFAULT NULL COMMENT '存储task运行时所需的额外信息',
    `is_expire`               TINYINT(1)   NOT NULL DEFAULT '0' COMMENT '过期策略：0永不过期 1过期取消',
    PRIMARY KEY (`id`),
    KEY `index_name` (`project_id`, `name`(128)),
    UNIQUE KEY `index_task_id` (`task_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8;

CREATE TABLE `schedule_task_task_shade`
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

CREATE TABLE `schedule_job`
(
    `id`              int(11)      NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)      NOT NULL COMMENT '租户id',
    `project_id`      int(11)      NOT NULL COMMENT '项目id',
    `dtuic_tenant_id` int(11)      NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)      NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `job_id`          varchar(256) NOT NULL COMMENT '工作任务id',
    `job_key`         varchar(256) NOT NULL DEFAULT '' COMMENT '工作任务key',
    `job_name`        VARCHAR(256) NOT NULL DEFAULT '' COMMENT '工作任务名称',
    `task_id`         int(11)      NOT NULL COMMENT '任务id',
    `gmt_create`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `create_user_id`  int(11)      NOT NULL COMMENT '发起操作的用户',
    `is_deleted`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    `type`            tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常调度 1补数据',
    `is_restart`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0：非重启任务, 1：重启任务',
    `business_date`   varchar(256) NOT NULL COMMENT '业务日期 yyyyMMddHHmmss',
    `cyc_time`        varchar(256) NOT NULL COMMENT '调度时间 yyyyMMddHHmmss',
    `dependency_type` tinyint(2)   NOT NULL DEFAULT 0 COMMENT '依赖类型',
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

CREATE TABLE `schedule_job_job`
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


CREATE TABLE `schedule_fill_data_job`
(
    `id`              int(11)     NOT NULL AUTO_INCREMENT,
    `tenant_id`       int(11)     NOT NULL COMMENT '租户id',
    `project_id`      int(11)     NOT NULL COMMENT '项目id',
    `dtuic_tenant_id` int(11)     NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`        int(11)     NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `job_name`        VARCHAR(64) NOT NULL DEFAULT '' COMMENT '补数据任务名称',
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


CREATE TABLE `schedule_job_graph_trigger`
(
    `id`           int(11)    NOT NULL AUTO_INCREMENT,
    `trigger_type` tinyint(3) NOT NULL COMMENT '0:正常调度 1补数据',
    `trigger_time` datetime   NOT NULL COMMENT '调度时间',
    `gmt_create`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`   int(10)    NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `index_trigger_time` (`trigger_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

