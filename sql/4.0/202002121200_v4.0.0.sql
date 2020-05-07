-- create
-- engine
CREATE TABLE `rdos_plugin_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `plugin_key` varchar(255) NOT NULL COMMENT '插件配置信息md5值',
  `plugin_info` text NOT NULL COMMENT '插件信息',
  `type` tinyint(2) NOT NULL COMMENT '类型 0:默认插件, 1:动态插件(暂时数据库只存动态插件)',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_plugin_id` (`plugin_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rdos_engine_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
  `job_id` varchar(256) NOT NULL COMMENT '离线任务id',
  `engine_job_id` varchar(256) DEFAULT NULL COMMENT '离线任务计算引擎id',
  `application_id` varchar(256) DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id',
  `job_name` varchar(256) DEFAULT NULL COMMENT '任务名称',
  `exec_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `exec_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `exec_time` int(11) DEFAULT '0' COMMENT '执行时间',
  `retry_num` int(10) NOT NULL DEFAULT '0',
  `log_info` mediumtext COMMENT '错误信息',
  `engine_log` longtext COMMENT '引擎错误信息',
  `plugin_info_id` int(11) DEFAULT NULL COMMENT '插件信息',
  `source_type` tinyint(2) DEFAULT NULL COMMENT '任务来源',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `version_id` int(11) DEFAULT NULL COMMENT '任务对应版本id',
  `retry_task_params` text DEFAULT NULL COMMENT '重试任务参数',
  `compute_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '计算类型STREAM(0), BATCH(1)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`(128),`is_deleted`),
  KEY `index_engine_job_id` (`engine_job_id`(128)),
  KEY `index_status` (`status`),
  KEY `index_gmt_modified` (`gmt_modified`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `rdos_stream_task_checkpoint` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(64) NOT NULL COMMENT '任务id',
  `task_engine_id` varchar(64) NOT NULL COMMENT '任务对于的引擎id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `checkpoint_id` varchar(64) DEFAULT NULL,
  `checkpoint_trigger` timestamp NULL DEFAULT NULL COMMENT 'checkpoint触发时间',
  `checkpoint_savepath` varchar(128) DEFAULT NULL COMMENT 'checkpoint存储路径',
  `checkpoint_counts` varchar(128) DEFAULT NULL COMMENT 'checkpoint信息中的counts指标',
  PRIMARY KEY (`id`),
  UNIQUE KEY `taskid_checkpoint` (`task_id`,`checkpoint_id`) COMMENT 'taskid和checkpoint组成的唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=26474 DEFAULT CHARSET=utf8;

CREATE TABLE `rdos_engine_job_cache` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(256) NOT NULL COMMENT '任务id',
  `job_name` VARCHAR(256) DEFAULT NULL COMMENT '任务名称',
  `engine_type` varchar(256) NOT NULL COMMENT '任务的执行引擎类型',
  `compute_type` tinyint(2) NOT NULL COMMENT '计算类型stream/batch',
  `stage` tinyint(2) NOT NULL COMMENT '处于master等待队列：1 还是exe等待队列 2',
  `job_info` longtext NOT NULL COMMENT 'job信息',
  `node_address` varchar(256) DEFAULT NULL COMMENT '节点地址',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `job_priority` BIGINT(20) DEFAULT NULL COMMENT '任务优先级',
  `group_name` VARCHAR(256) DEFAULT NULL COMMENT 'group name',
  `is_failover` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0：不是，1：由故障恢复来的任务',
  PRIMARY KEY (`id`),
  unique KEY `index_job_id` (`job_id`(128))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;


CREATE TABLE `rdos_plugin_job_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(255) NOT NULL COMMENT '任务id',
  `job_info` LONGTEXT NOT NULL COMMENT '任务信息',
  `log_info` text COMMENT '任务信息',
  `status` tinyint(2) NOT NULL COMMENT '任务状态',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rdos_engine_unique_sign` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unique_sign` varchar(255) NOT NULL COMMENT '唯一标识',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_unique_sign` (`unique_sign`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rdos_engine_job_retry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
  `job_id` varchar(256) NOT NULL COMMENT '离线任务id',
  `engine_job_id` varchar(256) DEFAULT NULL COMMENT '离线任务计算引擎id',
  `application_id` varchar(256) DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id',
  `exec_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `exec_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `retry_num` int(10) NOT NULL DEFAULT '0',
  `log_info` mediumtext COMMENT '错误信息',
  `engine_log` longtext COMMENT '引擎错误信息',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `retry_task_params` text DEFAULT NULL COMMENT '重试任务参数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


CREATE TABLE `rdos_engine_job_stop_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(256) NOT NULL COMMENT '任务id',
  `task_type` int(10) DEFAULT NULL COMMENT '任务类型',
  `engine_type` varchar(256) DEFAULT NULL COMMENT '任务的执行引擎类型',
  `compute_type` tinyint(2) DEFAULT NULL COMMENT '计算类型stream/batch',
  `group_name` VARCHAR(256) DEFAULT NULL COMMENT 'group name',
  `version` int(10) DEFAULT '0' COMMENT '版本号',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `operator_expired` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作过期时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `rdos_node_machine` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(64) NOT NULL COMMENT 'master主机ip',
  `port` int(11) NOT NULL COMMENT 'master主机端口',
  `machine_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 master,1 slave',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `app_type` varchar(64) NOT NULL DEFAULT 'web' COMMENT 'web,engine',
  `deploy_info` varchar(256) DEFAULT NULL COMMENT 'flink,spark对应的部署模式',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_machine` (`ip`,`port`)
) ENGINE=InnoDB AUTO_INCREMENT=1018 DEFAULT CHARSET=utf8;

-- console

CREATE TABLE `console_cluster` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(24) NOT NULL COMMENT '集群名称',
  `hadoop_version` varchar(24) NOT NULL COMMENT 'hadoop版本',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx` (`cluster_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `console_engine` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cluster_id` int(11) NOT NULL COMMENT '集群id',
  `engine_name` varchar(24) NOT NULL COMMENT '引擎名称',
  `engine_type` tinyint(4) NOT NULL COMMENT '引擎类型',
  `total_node` int(11) NOT NULL COMMENT '节点数',
  `total_memory` int(11) NOT NULL COMMENT '总内存',
  `total_core` int(11) NOT NULL COMMENT '总核数',
  `sync_type` tinyint(1) NULL COMMENT '获取元数据组件类型',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `console_component` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `engine_id` int(11) NOT NULL COMMENT '引擎id',
  `component_name` varchar(24) NOT NULL COMMENT '组件名称',
  `component_type_code` tinyint(1) NOT NULL COMMENT '组件类型',
  `component_config` text NOT NULL COMMENT '组件配置',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `console_dtuic_tenant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dt_uic_tenant_id` int(11) NOT NULL COMMENT 'uic租户id',
  `tenant_name` varchar(256) NOT NULL COMMENT '用户名称',
  `tenant_desc` varchar(256) DEFAULT '' COMMENT '租户描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `console_engine_tenant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `engine_id` int(11) NOT NULL COMMENT '引擎id',
  `queue_id` int(11) NULL COMMENT '队列id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `console_queue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `engine_id` int(11) NOT NULL COMMENT '引擎id',
  `queue_name` varchar(24) NOT NULL COMMENT '队列名称',
  `capacity` varchar(24) NOT NULL COMMENT '最小容量',
  `max_capacity` varchar(24) NOT NULL COMMENT '最大容量',
  `queue_state` varchar(24) NOT NULL COMMENT '运行状态',
  `parent_queue_id` int(11) NOT NULL COMMENT '父队列id',
  `queue_path` varchar(256) NOT NULL COMMENT '队列路径',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `console_kerberos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_id` int(11) NOT NULL COMMENT '集群id',
  `open_kerberos` tinyint(1) NOT NULL COMMENT '是否开启kerberos配置',
  `name` varchar(100) NOT NULL COMMENT 'kerberos文件名称',
  `remote_path` varchar(200) NOT NULL COMMENT 'sftp存储路径',
  `principal` varchar(50) NOT NULL COMMENT 'principal',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- task

CREATE TABLE `rdos_batch_task_shade`
(
    `id`                      int(11)      NOT NULL AUTO_INCREMENT,
    `tenant_id`               int(11)      NOT NULL DEFAULT '-1' COMMENT '租户id',
    `project_id`              int(11)      NOT NULL DEFAULT '-1' COMMENT '项目id',
    `dtuic_tenant_id`         int(11)      NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `app_type`                int(11)      NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `node_pid`                int(11)      NOT NULL DEFAULT '0' COMMENT '父文件夹id',
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
    `extra_info`              mediumtext                  DEFAULT NULL COMMENT '存储task运行时所需的额外信息',
    `is_expire`               TINYINT(1)   NOT NULL DEFAULT '0' COMMENT '过期策略：0永不过期 1过期取消',
    PRIMARY KEY (`id`),
    KEY `index_name` (`project_id`, `name`(128)),
    UNIQUE KEY `index_task_id` (`task_id`,`app_type`)
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

-- insert
-- engine
-- 如果插入的数据会导致UNIQUE索引或PRIMARY KEY发生冲突/重复，则忽略此次操作/不插入数据

insert IGNORE into rdos_plugin_info
select * from ide.rdos_plugin_info;

insert IGNORE into rdos_engine_job
select * from ide.rdos_engine_job;

insert IGNORE into rdos_stream_task_checkpoint
select * from ide.rdos_stream_task_checkpoint;

insert IGNORE into rdos_engine_job_cache
select * from ide.rdos_engine_job_cache;

insert IGNORE into rdos_plugin_job_info
select * from ide.rdos_plugin_job_info;

insert IGNORE into rdos_engine_unique_sign
select * from ide.rdos_engine_unique_sign;

insert IGNORE into rdos_engine_job_retry
select * from ide.rdos_engine_job_retry;

insert IGNORE into rdos_engine_job_stop_record
select * from ide.rdos_engine_job_stop_record;

insert IGNORE into rdos_node_machine
select * from ide.rdos_node_machine;

-- console

insert IGNORE into console_cluster
select * from console.console_cluster;

insert IGNORE into console_engine
select * from console.console_engine;

insert IGNORE into console_component
select * from console.console_component;

insert IGNORE into console_dtuic_tenant
select * from console.console_dtuic_tenant;

insert IGNORE into console_engine_tenant
select * from console.console_engine_tenant;

insert IGNORE into console_queue
select * from console.console_queue;

-- ide

insert IGNORE into rdos_batch_task_shade( tenant_id, project_id, dtuic_tenant_id, app_type, node_pid, name, task_type, engine_type, compute_type, sql_text, task_params, task_id, schedule_conf, period_type, schedule_status, project_schedule_status, submit_status, gmt_create, gmt_modified, modify_user_id, create_user_id, owner_user_id, version_id, is_deleted, task_desc, main_class, exe_args, flow_id, is_publish_to_produce, extra_info, is_expire)
select
       ts.tenant_id,
       ts.project_id,
       tr.dtuic_tenant_id,
       1,
       ts.node_pid,
       ts.name,
       ts.task_type,
       ts.engine_type,
       ts.compute_type,
       ts.sql_text,
       ts.task_params,
       ts.id,
       ts.schedule_conf,
       ts.period_type,
       ts.schedule_status,
       rp.schedule_status,
       ts.submit_status,
       ts.gmt_create,
       ts.gmt_modified,
       ts.modify_user_id,
       ts.create_user_id,
       ts.owner_user_id,
       ts.version,
       ts.is_deleted,
       ts.task_desc,
       ts.main_class,
       ts.exe_args,
       ts.flow_id,
       ts.is_publish_to_produce,
       '',
       0
from ide.rdos_batch_task_shade ts
         left join ide.rdos_tenant tr on ts.tenant_id = tr.id
left join ide.rdos_project rp on ts.project_id = rp.id;



insert IGNORE into rdos_batch_task_task_shade (tenant_id, project_id, dtuic_tenant_id, app_type, task_id, parent_task_id,
                                        gmt_create, gmt_modified, is_deleted)
select ts.tenant_id,
       ts.project_id,
       rt.dtuic_tenant_id,
       1,
       ts.task_id,
       ts.parent_task_id,
       ts.gmt_create,
       ts.gmt_modified,
       ts.is_deleted
from ide.rdos_batch_task_task_shade ts
         left join ide.rdos_tenant rt
                   on ts.tenant_id = rt.id;


insert IGNORE into rdos_batch_job_job (tenant_id, project_id, dtuic_tenant_id, app_type, job_key, parent_job_key,
                                     gmt_create, gmt_modified, is_deleted)
select bj.tenant_id,
       bj.project_id,
       (select dtuic_tenant_id from ide.rdos_tenant where ide.rdos_tenant.id = bj.tenant_id),
       1,
       bj.job_key,
       jj.parent_job_key,
       jj.gmt_create,
       jj.gmt_modified,
       jj.is_deleted

from ide.rdos_batch_job_job jj
         left join ide.rdos_batch_job bj on bj.job_key = jj.job_key;


insert IGNORE into rdos_batch_fill_data_job (tenant_id, project_id, dtuic_tenant_id, app_type, job_name, run_day,
                                                 from_day, to_day, gmt_create, gmt_modified, create_user_id, is_deleted)
select fdj.tenant_id,
       fdj.project_id,
       (select dtuic_tenant_id from ide.rdos_tenant where id = fdj.tenant_id),
       1,
       fdj.job_name,
       fdj.run_day,
       fdj.from_day,
       fdj.to_day,
       fdj.gmt_create,
       fdj.gmt_modified,
       fdj.create_user_id,
       fdj.is_deleted
from ide.rdos_batch_fill_data_job fdj;



-- 插入之后 在更新
insert IGNORE into rdos_batch_job(tenant_id, project_id, dtuic_tenant_id, app_type, job_id, job_key, job_name,
                                      task_id,
                                      gmt_create, gmt_modified, create_user_id, is_deleted, type, is_restart,
                                      business_date,
                                      cyc_time, dependency_type, flow_job_id, period_type, status, task_type, fill_id,
                                      exec_start_time, exec_end_time, exec_time, submit_time, retry_num, node_address,
                                      version_id, log_info, next_cyc_time, max_retry_num)

select tenant_id,
       project_id,
       -1,
       1,
       job_id,
       job_key,
       job_name,
       task_id,
       gmt_create,
       gmt_modified,
       create_user_id,
       is_deleted,
       type,
       is_restart,
       business_date,
       cyc_time,
       dependency_type,
       flow_job_id,
       period_type,
       -1,
       -1,
       -1,
       null,
       null,
       null,
       null,
       0,
       '',
       0,
       '',
       '',
       0
from ide.rdos_batch_job;

update rdos_batch_job rbj left join ide.rdos_engine_job rebj on rbj.job_id = rebj.job_id
set rbj.status          = IFNULL(rebj.status, 0),
    rbj.exec_start_time = rebj.exec_start_time,
    rbj.exec_end_time   = rebj.exec_end_time,
    rbj.exec_time       = rebj.exec_time,
    rbj.retry_num       = IFNULL(rebj.retry_num, 0),
    rbj.version_id      = rebj.version_id
where rbj.status = -1;

update rdos_batch_job rbj left join ide.rdos_batch_task bt on rbj.task_id = bt.id
set rbj.task_type = bt.task_type where bt.task_type is not null;


update rdos_batch_job rbj
set fill_id = (select fill_id
               from ide.rdos_batch_fill_data_relation fdr
               where rbj.id = fdr.job_id)
where type = 1;

update rdos_batch_job rbj
set dtuic_tenant_id = (select dtuic_tenant_id from ide.rdos_tenant where ide.rdos_tenant.id = rbj.tenant_id)
where dtuic_tenant_id = -1;

-- update

-- engine
-- engine新增字段


alter table rdos_engine_job_cache change `group_name` `job_resource` varchar(256)  DEFAULT '' COMMENT 'job的计算引擎资源类型';
alter table rdos_engine_job_stop_record change `group_name` `job_resource` varchar(256)  DEFAULT '' COMMENT 'job的计算引擎资源类型';
ALTER TABLE  `rdos_engine_job_stop_record` modify  COLUMN `engine_type` varchar(256) DEFAULT NULL COMMENT '任务的执行引擎类型';
ALTER TABLE  `rdos_engine_job_stop_record` modify  COLUMN `compute_type` tinyint(2) DEFAULT NULL COMMENT '计算类型stream/batch';

-- engine修改表名

ALTER TABLE rdos_plugin_info RENAME TO schedule_plugin_info;
ALTER TABLE rdos_engine_job RENAME TO schedule_engine_job;
ALTER TABLE rdos_stream_task_checkpoint RENAME TO schedule_engine_job_checkpoint;
ALTER TABLE rdos_engine_job_cache RENAME TO schedule_engine_job_cache;
ALTER TABLE rdos_plugin_job_info RENAME TO schedule_plugin_job_info;
ALTER TABLE rdos_engine_unique_sign RENAME TO schedule_engine_unique_sign;
ALTER TABLE rdos_engine_job_retry RENAME TO schedule_engine_job_retry;
ALTER TABLE rdos_engine_job_stop_record RENAME TO schedule_engine_job_stop_record;
ALTER TABLE rdos_node_machine RENAME TO schedule_node_machine;

-- engine新增comment

ALTER TABLE  `schedule_engine_job_checkpoint` modify  COLUMN `checkpoint_id` varchar(64) DEFAULT NULL COMMENT '检查点id';
ALTER TABLE  `schedule_engine_job_retry` modify  COLUMN `retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '执行时，重试的次数';
ALTER TABLE  `schedule_engine_job` modify  COLUMN `retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '执行时，重试的次数';
ALTER TABLE  `schedule_engine_job` modify  COLUMN `retry_task_params` text DEFAULT NULL COMMENT '重试任务参数';

-- task
-- task修改表名

ALTER TABLE rdos_batch_task_shade RENAME TO schedule_task_shade;
ALTER TABLE rdos_batch_task_task_shade RENAME TO schedule_task_task_shade;
ALTER TABLE rdos_batch_job RENAME TO schedule_job;
ALTER TABLE rdos_batch_job_job RENAME TO schedule_job_job;
ALTER TABLE rdos_batch_fill_data_job RENAME TO schedule_fill_data_job;
ALTER TABLE rdos_job_graph_trigger RENAME TO schedule_job_graph_trigger;

-- task新增comment

ALTER TABLE  `schedule_task_shade` modify  COLUMN `task_desc` varchar(256) NOT NULL COMMENT '任务描述';
ALTER TABLE  `schedule_task_shade` modify  COLUMN `main_class` varchar(256) NOT NULL COMMENT 'Jar包的入口函数';
ALTER TABLE  `schedule_task_shade` modify  COLUMN `exe_args` text DEFAULT NULL COMMENT '额外参数';
ALTER TABLE  `schedule_job` modify  COLUMN `job_key` varchar(256) NOT NULL DEFAULT '' COMMENT '工作任务key';
ALTER TABLE  `schedule_job` modify  COLUMN `job_name` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '工作任务名称';
ALTER TABLE  `schedule_job` modify  COLUMN `dependency_type` tinyint(2) NOT NULL DEFAULT 0  COMMENT '依赖类型';
ALTER TABLE  `schedule_fill_data_job` modify  COLUMN `job_name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '补数据任务名称';
ALTER TABLE  `schedule_job_graph_trigger` modify  COLUMN `trigger_time` datetime NOT NULL COMMENT '调度时间';


-- 将schedule_job shedule_engine_job 两表合并

ALTER TABLE  `schedule_job` modify  COLUMN `type`     tinyint(1) NOT NULL DEFAULT '2' COMMENT '0正常调度 1补数据 2临时运行';
ALTER TABLE  `schedule_job` modify  COLUMN `log_info` mediumtext                      COMMENT '错误信息';

ALTER TABLE  `schedule_job` ADD `engine_job_id`  varchar(256) DEFAULT NULL COMMENT '离线任务计算引擎id';
ALTER TABLE  `schedule_job` ADD `application_id`  varchar(256) DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id';
ALTER TABLE  `schedule_job` ADD `engine_log`      longtext                  COMMENT '引擎错误信息';
ALTER TABLE  `schedule_job` ADD `plugin_info_id`  int(11)      DEFAULT NULL COMMENT '插件信息';
ALTER TABLE  `schedule_job` ADD `source_type`     tinyint(2)   DEFAULT NULL COMMENT '任务来源';
ALTER TABLE  `schedule_job` ADD `retry_task_params` text       DEFAULT NULL COMMENT '重试任务参数';
ALTER TABLE  `schedule_job` ADD `compute_type`    tinyint(1)   NOT NULL DEFAULT '1' COMMENT '计算类型STREAM(0), BATCH(1)';

update `schedule_job` as sj left join `schedule_engine_job` as sej
    on sj.job_id = sej.job_id
    set sj.engine_job_id = sej.engine_job_id,
        sj.application_id = sej.application_id,
        sj.engine_log = sej.engine_log,
        sj.plugin_info_id = sej.plugin_info_id,
        sj.source_type = sej.source_type,
        sj.retry_task_params = sej.retry_task_params,
    sj.compute_type = sej.compute_type
        where sj.is_deleted = 0;

DROP TABLE IF EXISTS `schedule_engine_job`;



-- 账号表
CREATE TABLE `console_user`
(
    `id`                 int(11)      NOT NULL AUTO_INCREMENT,
    `dtuic_user_id`      int(11)      NOT NULL COMMENT 'dtuic userid',
    `user_name`          varchar(256) NOT NULL COMMENT '用户名称',
    `email`              varchar(256) NOT NULL COMMENT '用户手机号',
    `status`             tinyint(1)   NOT NULL DEFAULT '0' COMMENT '用户状态0：正常，1：禁用',
    `gmt_create`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`         tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    `default_project_id` int(11)               DEFAULT NULL COMMENT '默认项目id',
    `phone_number`       varchar(256)          DEFAULT NULL COMMENT '用户手机号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `index_dtuic_user_id` (`dtuic_user_id`),
    KEY `index_user_name` (`user_name`(128))
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8;

CREATE TABLE `console_account`
(
    `id`             int(11)      NOT NULL AUTO_INCREMENT,
    `name`           varchar(24)  NOT NULL COMMENT '用户名',
    `password`       varchar(256) NOT NULL COMMENT '密码',
    `type`           tinyint(1)   NOT NULL COMMENT '账号类型',
    `gmt_create`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`     tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    `create_user_id` int(11)      NOT NULL,
    `modify_user_id` int(11)               DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `console_account_tenant`
(
    `id`             int(11)    NOT NULL AUTO_INCREMENT,
    `account_id`     int(11)    NOT NULL COMMENT '数据库账号',
    `user_id`        int(11)    NOT NULL COMMENT '数栈绑定用户',
    `tenant_id`      int(11)    NOT NULL COMMENT '数栈绑定租户',
    `gmt_create`     datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`     tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    `create_user_id` int(11)    NOT NULL,
    `modify_user_id` int(11)             DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8;

-- 账号数据

insert IGNORE into console_user
select * from console.console_user;

insert IGNORE into console_account
select * from console.console_account;

insert IGNORE into console_account_tenant
select * from console.console_account_tenant;


-- task

insert IGNORE into schedule_task_shade( tenant_id, project_id, dtuic_tenant_id, app_type, node_pid, name, task_type, engine_type, compute_type, sql_text, task_params, task_id, schedule_conf, period_type, schedule_status, project_schedule_status, submit_status, gmt_create, gmt_modified, modify_user_id, create_user_id, owner_user_id, version_id, is_deleted, task_desc, main_class, exe_args, flow_id, is_publish_to_produce, extra_info, is_expire)
select
    ts.tenant_id,
    ts.project_id,
    ts.dtuic_tenant_id,
    ts.app_type,
    ts.node_pid,
    ts.name,
    ts.task_type,
    ts.engine_type,
    ts.compute_type,
    ts.sql_text,
    ts.task_params,
    ts.task_id,
    ts.schedule_conf,
    ts.period_type,
    ts.schedule_status,
    ts.project_schedule_status,
    ts.submit_status,
    ts.gmt_create,
    ts.gmt_modified,
    ts.modify_user_id,
    ts.create_user_id,
    ts.owner_user_id,
    ts.version_id,
    ts.is_deleted,
    ts.task_desc,
    ts.main_class,
    ts.exe_args,
    ts.flow_id,
    ts.is_publish_to_produce,
    ts.extra_info,
    ts.is_expire
from task.rdos_batch_task_shade ts;



insert IGNORE into schedule_task_task_shade (tenant_id, project_id, dtuic_tenant_id, app_type, task_id, parent_task_id,
                                             gmt_create, gmt_modified, is_deleted)
select ts.tenant_id,
       ts.project_id,
       ts.dtuic_tenant_id,
       ts.app_type,
       ts.task_id,
       ts.parent_task_id,
       ts.gmt_create,
       ts.gmt_modified,
       ts.is_deleted
from task.rdos_batch_task_task_shade ts;




insert IGNORE into schedule_job_job (tenant_id, project_id, dtuic_tenant_id, app_type, job_key, parent_job_key,
                                     gmt_create, gmt_modified, is_deleted)
select jj.tenant_id,
       jj.project_id,
       jj.dtuic_tenant_id,
       jj.app_type,
       jj.job_key,
       jj.parent_job_key,
       jj.gmt_create,
       jj.gmt_modified,
       jj.is_deleted
from task.rdos_batch_job_job jj;


INSERT IGNORE INTO schedule_job(`tenant_id`, `project_id`, `dtuic_tenant_id`, `app_type`, `job_id`, `job_key`, `job_name`, `task_id`, `gmt_create`, `gmt_modified`, `create_user_id`,
                                `is_deleted`, `type`, `is_restart`, `business_date`, `cyc_time`, `dependency_type`,`flow_job_id`, `period_type`, `status`, `task_type`, `fill_id`, `exec_start_time`, `exec_end_time`,
                                `exec_time`, `submit_time`, `max_retry_num`, `retry_num`, `log_info`, `node_address`, `version_id`, `next_cyc_time`, `engine_job_id`, `application_id`, `engine_log`, `plugin_info_id`,
                                `source_type`, `retry_task_params`, `compute_type`)
select bj.tenant_id,
       bj.project_id,
       bj.dtuic_tenant_id,
       bj.app_type,
       bj.job_id,
       bj.job_key,
       bj.job_name,
       bj.task_id,
       bj.gmt_create,
       bj.gmt_modified,
       bj.create_user_id,
       bj.is_deleted,
       bj.type,
       bj.is_restart,
       bj.business_date,
       bj.cyc_time,
       bj.dependency_type,
       bj.flow_job_id,
       bj.period_type,
       IFNULL(rej.status,bj.status),
       bj.task_type,
       bj.fill_id,
       bj.exec_start_time,
       bj.exec_end_time,
       bj.exec_time,
       bj.submit_time,
       bj.max_retry_num,
       bj.retry_num,
       rej.log_info,
       bj.node_address,
       rej.version_id,
       bj.next_cyc_time,
       rej.engine_job_id,
       rej.application_id,
       rej.engine_log,
       rej.plugin_info_id,
       rej.source_type,
       rej.retry_task_params,
       IFNULL(rej.compute_type,1)
from task.rdos_batch_job bj LEFT JOIN ide.rdos_engine_job rej on bj.job_id = rej.job_id;

