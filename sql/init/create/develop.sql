CREATE TABLE `rdos_batch_catalogue` (
                                        `id` int(11) NOT NULL AUTO_INCREMENT,
                                        `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                        `node_name` varchar(128) NOT NULL COMMENT '文件夹名称',
                                        `node_pid` int(11) NOT NULL DEFAULT '-1' COMMENT '父文件夹id -1:没有上级目录',
                                        `order_val` int(3) DEFAULT NULL,
                                        `level` tinyint(1) NOT NULL DEFAULT '3' COMMENT '目录层级 0:一级 1:二级 n:n+1级',
                                        `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                        `create_user_id` int(11) NOT NULL COMMENT '创建用户',
                                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                        `catalogue_type` tinyint(1) DEFAULT '0' COMMENT '目录类型 0任务目录 1 项目目录',
                                        PRIMARY KEY (`id`),
                                        KEY `index_catologue_name` (`node_pid`,`node_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件夹、目录表';

CREATE TABLE `rdos_batch_function` (
                                       `id` int(11) NOT NULL AUTO_INCREMENT,
                                       `name` varchar(512) NOT NULL COMMENT '函数名称',
                                       `class_name` varchar(512) DEFAULT NULL COMMENT 'main函数类名',
                                       `purpose` varchar(1024) DEFAULT NULL COMMENT '函数用途',
                                       `command_formate` varchar(1024) DEFAULT NULL COMMENT '函数命令格式',
                                       `param_desc` varchar(1024) DEFAULT NULL COMMENT '函数参数说明',
                                       `node_pid` int(11) NOT NULL COMMENT '父文件夹id',
                                       `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                       `create_user_id` int(11) NOT NULL COMMENT '创建的用户',
                                       `modify_user_id` int(11) NOT NULL COMMENT '创建的用户',
                                       `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0自定义 1系统',
                                       `task_type` int(11) NOT NULL DEFAULT '0' COMMENT '0: SparkSQL ',
                                       `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                       `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                       `sql_text` text COMMENT 'sql文本',
                                       PRIMARY KEY (`id`),
                                       KEY `index_rdos_batch_function` (`name`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='函数管理表';

CREATE TABLE `rdos_batch_function_resource` (
                                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                                `function_id` int(11) NOT NULL COMMENT '函数id',
                                                `resource_id` int(11) NOT NULL COMMENT '对应batch资源的id',
                                                `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                                `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                                `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                                `project_id` bigint(20) DEFAULT NULL,
                                                `tenant_id` bigint(20) DEFAULT NULL,
                                                `resourceId` bigint(20) DEFAULT NULL,
                                                PRIMARY KEY (`id`),
                                                UNIQUE KEY `index_rdos_function_resource` (`function_id`,`resource_id`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='函数关联的资源表';

CREATE TABLE `rdos_batch_hive_select_sql` (
                                              `id` int(11) NOT NULL AUTO_INCREMENT,
                                              `job_id` varchar(256) NOT NULL COMMENT '工作任务id',
                                              `temp_table_name` varchar(256) NOT NULL COMMENT '临时表名',
                                              `is_select_sql` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-否 1-是',
                                              `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                              `user_id` int(11) DEFAULT NULL COMMENT '执行用户',
                                              `sql_text` longtext COMMENT 'sql',
                                              `parsed_columns` longtext COMMENT '字段信息',
                                              `task_type` int(11) DEFAULT NULL COMMENT '任务类型',
                                              `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                              `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                              `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                              PRIMARY KEY (`id`),
                                              UNIQUE KEY `idx` (`job_id`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='sql查询临时表';

CREATE TABLE `rdos_batch_resource` (
                                       `id` int(11) NOT NULL AUTO_INCREMENT,
                                       `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                       `node_pid` int(11) NOT NULL COMMENT '父文件夹id',
                                       `url` varchar(1028) NOT NULL COMMENT '资源路径',
                                       `resource_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '资源类型 0:other, 1:jar, 2:py, 3:zip, 4:egg',
                                       `resource_name` varchar(256) NOT NULL COMMENT '资源名称',
                                       `origin_file_name` varchar(256) NOT NULL COMMENT '源文件名',
                                       `resource_desc` varchar(256) NOT NULL COMMENT '源文描述',
                                       `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                       `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `create_user_id` int(11) NOT NULL COMMENT '新建资源的用户',
                                       `modify_user_id` int(11) NOT NULL COMMENT '修改人',
                                       `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                       `node_id` bigint(20) DEFAULT NULL,
                                       PRIMARY KEY (`id`),
                                       KEY `index_resource_name` (`resource_name`(128)),
                                       KEY `index_resource_type` (`resource_type`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资源表';

CREATE TABLE `rdos_batch_sys_parameter` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `param_name` varchar(64) NOT NULL COMMENT '参数名称',
                                            `param_command` varchar(64) NOT NULL COMMENT '参数替换指令',
                                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                            `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务开发-系统参数表';

CREATE TABLE `rdos_batch_task` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                   `node_pid` int(11) NOT NULL COMMENT '父文件夹id',
                                   `name` varchar(256) NOT NULL COMMENT '任务名称',
                                   `task_type` tinyint(1) NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL ',
                                   `compute_type` tinyint(1) NOT NULL COMMENT '计算类型 0实时，1 离线',
                                   `sql_text` longtext NOT NULL COMMENT 'sql 文本',
                                   `task_params` text NOT NULL COMMENT '任务参数',
                                   `schedule_conf` varchar(512) NOT NULL COMMENT '调度配置 json格式',
                                   `period_type` tinyint(2) DEFAULT NULL COMMENT '周期类型',
                                   `schedule_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0未开始,1正常调度,2暂停',
                                   `submit_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0未提交,1已提交',
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `modify_user_id` int(11) NOT NULL COMMENT '最后修改task的用户',
                                   `create_user_id` int(11) NOT NULL COMMENT '新建task的用户',
                                   `owner_user_id` int(11) NOT NULL COMMENT '负责人id',
                                   `version` int(11) NOT NULL DEFAULT '0' COMMENT 'task版本',
                                   `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                   `task_desc` varchar(256) NOT NULL,
                                   `main_class` varchar(256) NOT NULL,
                                   `exe_args` text,
                                   `flow_id` int(11) NOT NULL DEFAULT '0' COMMENT '工作流id',
                                   `component_version` varchar(25) DEFAULT NULL COMMENT '组件版本',
                                   PRIMARY KEY (`id`),
                                   KEY `index_name` (`name`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务表';


CREATE TABLE `rdos_batch_task_param` (
                                         `id` int(11) NOT NULL AUTO_INCREMENT,
                                         `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                         `type` int(2) NOT NULL COMMENT '0:系统参数, 1:自定义参数',
                                         `param_name` varchar(64) NOT NULL COMMENT '参数名称',
                                         `param_command` varchar(64) NOT NULL COMMENT '参数替换指令',
                                         `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                         `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                         `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                         PRIMARY KEY (`id`),
                                         KEY `index_batch_task_parameter` (`task_id`,`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务开发-任务参数配置表';

CREATE TABLE `rdos_batch_task_param_shade` (
                                               `id` int(11) NOT NULL AUTO_INCREMENT,
                                               `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                               `type` int(2) NOT NULL COMMENT '0:系统参数, 1:自定义参数',
                                               `param_name` varchar(64) NOT NULL COMMENT '参数名称',
                                               `param_command` varchar(64) NOT NULL COMMENT '参数替换指令',
                                               `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                               `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                               `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                               PRIMARY KEY (`id`),
                                               KEY `index_batch_task_parameter` (`task_id`,`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务参数配置- 提交表';

CREATE TABLE `rdos_batch_task_resource` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                            `resource_id` int(11) DEFAULT NULL COMMENT '对应batch资源的id',
                                            `resource_type` int(11) DEFAULT NULL COMMENT '使用资源的类型 1:主体资源, 2:引用资源',
                                            `project_id` int(11) NOT NULL COMMENT '项目id',
                                            `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                            `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `index_project_task_resource_id` (`project_id`,`task_id`,`resource_id`,`resource_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务和资源关联表';

CREATE TABLE `rdos_batch_task_resource_shade` (
                                                  `id` int(11) NOT NULL AUTO_INCREMENT,
                                                  `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                                  `resource_id` int(11) DEFAULT NULL COMMENT '对应batch资源的id',
                                                  `resource_type` int(11) DEFAULT NULL COMMENT '使用资源的类型 1:主体资源, 2:引用资源',
                                                  `project_id` int(11) NOT NULL COMMENT '项目id',
                                                  `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                                  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                                  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                                  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                                  PRIMARY KEY (`id`),
                                                  UNIQUE KEY `index_project_task_resource_shade_id` (`project_id`,`task_id`,`resource_id`,`resource_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务资源关联信息- 提交表';

CREATE TABLE `rdos_batch_task_task` (
                                        `id` int(11) NOT NULL AUTO_INCREMENT,
                                        `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                        `parent_task_id` int(11) DEFAULT NULL COMMENT '对应batch任务父节点的id',
                                        `project_id` int(11) NOT NULL COMMENT '项目id',
                                        `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                        `parent_apptype` int(2) NOT NULL DEFAULT '1' COMMENT '对应任务父节点的产品类型',
                                        `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                        `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `index_batch_task_task` (`project_id`,`parent_task_id`,`task_id`,`parent_apptype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务上下游关联关系表';

CREATE TABLE `rdos_batch_task_template` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `task_type` tinyint(2) NOT NULL COMMENT '任务类型',
                                            `type` tinyint(2) NOT NULL COMMENT '1-ods  2-dwd  3-dws  4-ads  5-dim',
                                            `content` text NOT NULL COMMENT '任务内容',
                                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                            `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务模板字典表';

CREATE TABLE `rdos_batch_task_version` (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                           `task_id` int(11) NOT NULL COMMENT '父文件夹id',
                                           `origin_sql` longtext COMMENT '原始sql',
                                           `sql_text` longtext NOT NULL COMMENT 'sql 文本',
                                           `publish_desc` text NOT NULL COMMENT '任务参数',
                                           `create_user_id` int(11) NOT NULL COMMENT '新建的用户',
                                           `version` int(11) NOT NULL DEFAULT '0' COMMENT 'task版本',
                                           `task_params` text NOT NULL COMMENT '任务参数',
                                           `schedule_conf` varchar(512) NOT NULL COMMENT '调度配置 json格式',
                                           `schedule_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0未开始,1正常调度,2暂停',
                                           `dependency_task_ids` text NOT NULL COMMENT '依赖的任务id，多个以,号隔开',
                                           `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                           `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                           `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务具体版本信息表';

CREATE TABLE `rdos_dict` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `type` int(11) NOT NULL DEFAULT '0' COMMENT '区分字典类型，1：数据源字典 ...',
                             `dict_name` varchar(256) NOT NULL DEFAULT '' COMMENT '字典名',
                             `dict_value` int(11) NOT NULL DEFAULT '0' COMMENT '字典值',
                             `dict_name_zh` varchar(256) NOT NULL DEFAULT '' COMMENT '字典中文名',
                             `dict_name_en` varchar(256) NOT NULL DEFAULT '' COMMENT '字典英文名',
                             `dict_sort` int(11) NOT NULL DEFAULT '0' COMMENT '字典顺序',
                             `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                             `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                             `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `index_type_dict_name` (`type`,`dict_name`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='字典表';

CREATE TABLE `rdos_read_write_lock` (
                                        `id` int(11) NOT NULL AUTO_INCREMENT,
                                        `lock_name` varchar(256) NOT NULL COMMENT '锁名称',
                                        `tenant_id` int(11) DEFAULT NULL COMMENT '租户Id',
                                        `relation_id` int(11) NOT NULL COMMENT 'Id',
                                        `type` varchar(256) NOT NULL COMMENT '任务类型 ',
                                        `create_user_id` int(11) DEFAULT NULL COMMENT '创建人Id',
                                        `modify_user_id` int(11) NOT NULL COMMENT '修改的用户',
                                        `version` int(11) NOT NULL DEFAULT '1' COMMENT '乐观锁,0是特殊含义',
                                        `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                        `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `index_read_write_lock` (`lock_name`(128)),
                                        UNIQUE KEY `index_lock` (`relation_id`,`type`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='读写锁记录表';

CREATE TABLE `rdos_tenant_component` (
                                         `id` int(11) NOT NULL AUTO_INCREMENT,
                                         `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                         `task_type` tinyint(1) NOT NULL COMMENT '任务类型',
                                         `component_identity` varchar(256) NOT NULL COMMENT '组件的标识信息，也就是组件配置的dbname',
                                         `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '项目状态0：初始化，1：正常,2:禁用,3:失败',
                                         `create_user_id` int(11) DEFAULT NULL COMMENT '创建人id',
                                         `modify_user_id` int(11) DEFAULT NULL COMMENT '修改人id',
                                         `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                         `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                         `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目与engine的关联关系表';


