CREATE TABLE `rdos_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `project_name` varchar(256) NOT NULL COMMENT '项目名称',
  `project_alias` varchar(256) NOT NULL COMMENT '项目别名',
  `project_Identifier` varchar(256) NOT NULL COMMENT '项目标识',
  `project_desc` varchar(4000) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '项目状态0：初始化，1：正常,2:禁用,3:失败',
  `create_user_id` int(11) NOT NULL COMMENT '新建项目的用户id',
  `modify_user_id` int(11) COMMENT '修改人id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `project_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '项目类型:0-普通项目，1-测试项目，2-生产项目',
  `produce_project_id` int(11) NULL COMMENT '绑定的生产项目id',
  `schedule_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '调度状态：0-开启，1-关闭',
  `is_allow_download` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否允许下载查询结果 1-正常 0-禁用',
  `catalogue_id` int(11) NOT NULL DEFAULT '0' COMMENT '目录id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT ='项目表';


CREATE TABLE `rdos_project_engine` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `engine_type` tinyint(1) NOT NULL COMMENT '多引擎的类型 0:flink, 1:spark, 2:datax, 3:learning, 4:shell, 5:python2, 6:dtyarnshell, 7:python3, 8:hadoop, 9:carbon, 10:postgresql, 11:kylin, 12:hive',
  `engine_identity` varchar(256) NOT NULL COMMENT '标识信息，比如hive的dbname',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '项目状态0：初始化，1：正常,2:禁用,3:失败',
  `create_user_id` int(11) COMMENT '创建人id',
  `modify_user_id` int(11) COMMENT '修改人id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT ='项目与engine的关联关系表';

CREATE TABLE `rdos_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '区分字典类型，1：数据源字典 ...',
  `dict_name` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '字典名',
  `dict_value` int(11) NOT NULL DEFAULT '0' COMMENT '字典值',
  `dict_name_zh` varchar(256) NOT NULL DEFAULT '' COMMENT '字典中文名',
  `dict_name_en` varchar(256) NOT NULL DEFAULT '' COMMENT '字典英文名',
  `dict_sort` int(11) NOT NULL DEFAULT '0' COMMENT '字典顺序',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_type_dict_name` (`type`,`dict_name`(128))
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT ='字典表';



CREATE TABLE `rdos_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(256) NOT NULL DEFAULT '' COMMENT '权限CODE',
  `name` varchar(256) DEFAULT NULL COMMENT '权限名',
  `display` varchar(256) DEFAULT NULL COMMENT '展示名称',
  `parent_id` int(11) DEFAULT NULL COMMENT '父权限id',
  `type` tinyint(1) DEFAULT '1' COMMENT '1:功能权限;2:数据权限',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='权限表';

CREATE TABLE `rdos_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT '角色所属租户id,0:基础角色',
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `role_name` varchar(256) NOT NULL COMMENT '角色名称',
  `role_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '角色类型：1：功能权限',
  `role_value` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'TEANTOWNER(1), PROJECTOWNER(2), PROJECTADMIN(3), MEMBER(4), OPERATION(5), DATADEVELOP(6)',
  `role_desc` varchar(256) NOT NULL DEFAULT '' COMMENT '角色描述',
  `modify_user_id` int(11) COMMENT '修改的用户',
  `create_user_id` int(11) COMMENT '创建的用户',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='角色表';


CREATE TABLE `rdos_role_permission` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `role_id` int(11) unsigned NOT NULL COMMENT '关联角色id',
  `permission_id` int(11) unsigned NOT NULL COMMENT '关联权限id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `modify_user_id` int(11) COMMENT '修改的用户',
  `create_user_id` int(11) COMMENT '创建的用户',
  `tenant_id` int(11) COMMENT '角色所属租户id,0:基础角色',
  `project_id` int(11) COMMENT '项目id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='角色权限关联表';

CREATE TABLE `rdos_role_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `project_id` int(11) NOT NULL DEFAULT '-1' COMMENT '项目id',
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `modify_user_id` int(11) COMMENT '修改的用户',
  `create_user_id` int(11) COMMENT '创建的用户',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_project_user_role` (`project_id`,`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='角色用户关联表';

CREATE TABLE `rdos_read_write_lock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lock_name` varchar(256) NOT NULL COMMENT '锁名称',
  `tenant_id` int(11) COMMENT '租户Id',
  `project_id` int(11) NOT NULL COMMENT '项目Id',
  `relation_id` int(11) NOT NULL COMMENT 'Id',
  `type` varchar(256) NOT NULL COMMENT '任务类型 ',
  `create_user_id` int(11) COMMENT '创建人Id',
  `modify_user_id` int(11) NOT NULL COMMENT '修改的用户',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '乐观锁,0是特殊含义',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_read_write_lock` (`lock_name`(128)),
  UNIQUE KEY `index_lock` (`project_id`,`relation_id`,`type`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='读写锁记录表';


CREATE TABLE `rdos_batch_catalogue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `node_name` varchar(128) NOT NULL COMMENT '文件夹名称',
  `node_pid` int(11) NOT NULL DEFAULT '-1' COMMENT '父文件夹id -1:没有上级目录',
  `order_val` int(3) DEFAULT NULL,
  `level` tinyint(1) NOT NULL DEFAULT '3' COMMENT '目录层级 0:一级 1:二级 n:n+1级',
  `engine_type` INT   DEFAULT 0   NOT NULL COMMENT '1: hadoop 2:libra 3:kylin',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` int(11) NOT NULL COMMENT '创建用户',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  catalogue_type TINYINT(1) DEFAULT '0' COMMENT '目录类型 0任务目录 1 项目目录',
  PRIMARY KEY (`id`),
  KEY `index_catologue_name` (`project_id`,`node_pid`,`node_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8 COMMENT ='文件夹、目录表';


CREATE TABLE `rdos_batch_data_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_name` varchar(128) NOT NULL COMMENT '数据源名称',
  `data_desc` varchar(1024) NOT NULL COMMENT '数据源描述',
  `data_json` TEXT NOT NULL COMMENT '链接数据源信息需要加密 json格式',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '数据源类型 1:mysql, 2:oracle, 3:sqlserver, 4:postgresql, 5:rdbms, 6:hdfs, 7:hive, 8:hbase, 9:ftp, 10:maxcompute, 11:es, 12:redis, 13:mongodb, 14:kafka_11, 15:ads, 16:beats, 17:kafka_10, 18:kafka_09, 19:db2, 20:carbondata, 21:libra, 22:gbase_8a, 23:kylin',
  `active` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0：未启用，1：使用中',
  `link_state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0：连接丢失，1：连接可用',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `create_user_id` int(11) NOT NULL COMMENT '创建的用户',
  `modify_user_id` int(11) NOT NULL COMMENT '修改的用户',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `is_default` TINYINT DEFAULT 0 NULL,
  PRIMARY KEY (`id`),
  KEY `index_rdos_batch_data_source` (`tenant_id`,`project_id`,`data_name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT ='数据源配置表';

CREATE TABLE `rdos_batch_function` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(512) NOT NULL COMMENT '函数名称',
  `class_name` VARCHAR(512) DEFAULT NULL COMMENT 'main函数类名',
  `purpose` varchar(1024) DEFAULT NULL COMMENT '函数用途',
  `command_formate` varchar(1024) DEFAULT NULL COMMENT '函数命令格式',
  `param_desc` varchar(1024) DEFAULT NULL COMMENT '函数参数说明',
  `node_pid` int(11) NOT NULL COMMENT '父文件夹id',
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `create_user_id` int(11) NOT NULL COMMENT '创建的用户',
  `modify_user_id` int(11) NOT NULL COMMENT '创建的用户',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0自定义 1系统',
  `engine_type` int DEFAULT 0 NOT NULL COMMENT '1: hadoop 2:libra 3:kylin',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `sql_text` text DEFAULT NULL COMMENT 'sql文本',
  PRIMARY KEY (`id`),
  KEY `index_rdos_batch_function` (`project_id`,`name`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='函数管理表';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='函数关联的资源表';


CREATE TABLE `rdos_batch_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `project_id` int(11) NOT NULL COMMENT '项目id',
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
  KEY `index_resource_name` (`project_id`,`resource_name`(128)),
  KEY `index_resource_type` (`resource_type`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT ='资源表';


CREATE TABLE `rdos_batch_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `node_pid` int(11) NOT NULL COMMENT '父文件夹id',
  `name` varchar(256) NOT NULL COMMENT '任务名称',
  `task_type` tinyint(1) NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL ',
  `engine_type` tinyint(1) NOT NULL COMMENT '执行引擎类型 0:flink, 1:spark, 2:datax, 3:learning, 4:shell, 5:python2, 6:dtyarnshell, 7:python3, 8:hadoop, 9:carbon, 10:postgresql, 11:kylin, 12:hive',
  `compute_type` tinyint(1) NOT NULL COMMENT '计算类型 0实时，1 离线',
  `sql_text` LONGTEXT NOT NULL COMMENT 'sql 文本',
  `task_params` text NOT NULL COMMENT '任务参数',
  `schedule_conf` varchar(512) NOT NULL COMMENT '调度配置 json格式',
  `period_type` tinyint(2) COMMENT '周期类型',
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
  `exe_args` text DEFAULT NULL,
  `flow_id` INT ( 11 ) NOT NULL DEFAULT '0' COMMENT '工作流id',
  `component_version` varchar(25) DEFAULT NULL COMMENT '组件版本',
  PRIMARY KEY (`id`),
  KEY `index_name` (`project_id`,`name`(128))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT ='任务表';

CREATE TABLE `rdos_batch_task_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `task_id` int(11) NOT NULL COMMENT '父文件夹id',
  `origin_sql` LONGTEXT COMMENT '原始sql',
  `sql_text` LONGTEXT NOT NULL COMMENT 'sql 文本',
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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT ='任务具体版本信息表';

CREATE TABLE `rdos_batch_task_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
  `resource_id` int(11) DEFAULT NULL COMMENT '对应batch资源的id',
  `resource_type` int(11) COMMENT '使用资源的类型 1:主体资源, 2:引用资源',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_project_task_resource_id` (`project_id`,`task_id`,`resource_id`,`resource_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='任务和资源关联表';


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='任务上下游关联关系表';


CREATE TABLE `rdos_batch_sys_parameter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `param_name` VARCHAR(64) NOT NULL COMMENT '参数名称',
  `param_command` VARCHAR(64) NOT NULL COMMENT '参数替换指令',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='任务开发-系统参数表';


CREATE TABLE `rdos_batch_task_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
  `type` int(2) NOT NULL COMMENT '0:系统参数, 1:自定义参数',
  `param_name` VARCHAR(64) NOT NULL COMMENT '参数名称',
  `param_command` VARCHAR(64) NOT NULL COMMENT '参数替换指令',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_batch_task_parameter` (`task_id`, `param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='任务开发-任务参数配置表';


CREATE TABLE `rdos_batch_data_source_task_ref` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_source_id` int(11) NOT NULL COMMENT '任务id',
  `task_id` int(11) DEFAULT NULL COMMENT '对应资源的id,只有MR才需要添加资源id',
  `project_id` int(11) COMMENT '项目id',
  `tenant_id` int(11) COMMENT '租户id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_data_source_task_id` (`data_source_id`,`task_id`),
  KEY `index_task_id` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT ='数据源和任务的关联表';

CREATE TABLE `rdos_batch_task_resource_shade` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
  `resource_id` int(11) DEFAULT NULL COMMENT '对应batch资源的id',
  `resource_type` int(11) COMMENT '使用资源的类型 1:主体资源, 2:引用资源',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_project_task_resource_shade_id` (`project_id`,`task_id`,`resource_id`,`resource_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='任务资源关联信息- 提交表';

CREATE TABLE `rdos_batch_task_param_shade` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
  `type` int(2) NOT NULL COMMENT '0:系统参数, 1:自定义参数',
  `param_name` VARCHAR(64) NOT NULL COMMENT '参数名称',
  `param_command` VARCHAR(64) NOT NULL COMMENT '参数替换指令',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_batch_task_parameter` (`task_id`, `param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='任务参数配置- 提交表';


CREATE TABLE `rdos_batch_hive_select_sql` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(256) NOT NULL COMMENT '工作任务id',
  `temp_table_name` varchar(256) NOT NULL COMMENT '临时表名',
  `is_select_sql` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-否 1-是',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `user_id` int(11) NULL COMMENT '执行用户',
  `sql_text` LONGTEXT NULL COMMENT 'sql',
  `parsed_columns` LONGTEXT NULL COMMENT '字段信息',
  `engine_type` int null COMMENT '引擎类型',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `other_type` int null COMMENT '其他类型：0 默认，1 Impala',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx` (`job_id`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='sql查询临时表';


-- 默认离线任务模版
CREATE TABLE `rdos_batch_task_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_type` tinyint(2) NOT NULL COMMENT '任务类型',
  `type` tinyint(2) NOT NULL COMMENT '1-ods  2-dwd  3-dws  4-ads  5-dim',
  `content` text NOT NULL COMMENT '任务内容',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT ='任务模板字典表';


create table `rdos_batch_task_record` (
    `id` int(11) NOT NULL auto_increment,
    `project_id` int(11) NOT NULL COMMENT '项目id',
    `tenant_id` int(11) NOT NULL COMMENT '租户id',
    `record_type` int(2) NOT NULL COMMENT '记录类型',
    `task_id` int(11) NOT NULL COMMENT '任务id',
    `operator_id` int(11) NOT NULL COMMENT '操作者id',
    `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='任务变更记录表';