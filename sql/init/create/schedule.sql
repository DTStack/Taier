-- -------------------------------------------------------------
-- TablePlus 3.11.0(352)
--
-- https://tableplus.com/
--
-- Database: dag_beta
-- Generation Time: 2022-01-26 20:24:32.6110
-- -------------------------------------------------------------


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


DROP TABLE IF EXISTS `console_cluster`;
CREATE TABLE `console_cluster` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(128) NOT NULL COMMENT '集群名称',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cluster_name` (`cluster_name`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `console_cluster_tenant`;
CREATE TABLE `console_cluster_tenant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `cluster_id` int(11) NOT NULL COMMENT '集群id',
  `queue_id` int(11) DEFAULT NULL COMMENT '队列id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `console_component`;
CREATE TABLE `console_component` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `engine_id` int(11) DEFAULT NULL COMMENT '引擎id',
  `component_name` varchar(24) NOT NULL COMMENT '组件名称',
  `component_type_code` tinyint(1) NOT NULL COMMENT '组件类型',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `hadoop_version` varchar(25) DEFAULT '' COMMENT '组件hadoop版本',
  `upload_file_name` varchar(50) DEFAULT '' COMMENT '上传文件zip名称',
  `kerberos_file_name` varchar(50) DEFAULT '' COMMENT '上传kerberos文件zip名称',
  `store_type` tinyint(1) DEFAULT '4' COMMENT '组件存储类型: HDFS、NFS 默认HDFS',
  `is_metadata` tinyint(1) DEFAULT '0' COMMENT '/*1 metadata*/',
  `is_default` tinyint(1) NOT NULL DEFAULT '1' COMMENT '组件默认版本',
  `deploy_type` tinyint(1) DEFAULT NULL COMMENT '/* 0 standalone 1 yarn  */',
  `cluster_id` int(11) DEFAULT NULL COMMENT '集群id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_component` (`engine_id`,`component_type_code`,`hadoop_version`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `console_component_config`;
CREATE TABLE `console_component_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cluster_id` int(11) NOT NULL COMMENT '集群id',
  `component_id` int(11) NOT NULL COMMENT '组件id',
  `component_type_code` tinyint(1) NOT NULL COMMENT '组件类型',
  `type` varchar(128) NOT NULL COMMENT '配置类型',
  `required` tinyint(1) NOT NULL COMMENT 'true/false',
  `key` varchar(256) NOT NULL COMMENT '配置键',
  `value` text COMMENT '默认配置项',
  `values` varchar(512) DEFAULT NULL COMMENT '可配置项',
  `dependencyKey` varchar(256) DEFAULT NULL COMMENT '依赖键',
  `dependencyValue` varchar(256) DEFAULT NULL COMMENT '依赖值',
  `desc` varchar(512) DEFAULT NULL COMMENT '描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_componentId` (`component_id`),
  KEY `index_cluster_id` (`cluster_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12403 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `console_kerberos`;
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
  `krb_name` varchar(26) DEFAULT NULL COMMENT 'krb5_conf名称',
  `component_type` int(11) DEFAULT NULL COMMENT '组件类型',
  `principals` text COMMENT 'keytab用户文件列表',
  `merge_krb_content` text COMMENT '合并后的krb5',
  `component_version` varchar(25) DEFAULT NULL COMMENT '组件版本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `console_queue`;
CREATE TABLE `console_queue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `engine_id` int(11) DEFAULT NULL COMMENT '队列id',
  `queue_name` varchar(128) NOT NULL COMMENT '队列名称',
  `capacity` varchar(24) NOT NULL COMMENT '最小容量',
  `max_capacity` varchar(24) NOT NULL COMMENT '最大容量',
  `queue_state` varchar(24) NOT NULL COMMENT '运行状态',
  `parent_queue_id` int(11) NOT NULL COMMENT '父队列id',
  `queue_path` varchar(256) NOT NULL COMMENT '队列路径',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `cluster_id` int(11) DEFAULT NULL COMMENT '集群id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `console_user`;
CREATE TABLE `console_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(256) NOT NULL COMMENT '用户名称',
  `password` varchar(128) NOT NULL,
  `phone_number` varchar(256) DEFAULT NULL COMMENT '用户手机号',
  `email` varchar(256) NOT NULL COMMENT '用户手机号',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '用户状态0：正常，1：禁用',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_user_name` (`user_name`(128))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_dict`;
CREATE TABLE `schedule_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dict_code` varchar(64) NOT NULL COMMENT '字典标识',
  `dict_name` varchar(64) DEFAULT NULL COMMENT '字典名称',
  `dict_value` text COMMENT '字典值',
  `dict_desc` text COMMENT '字典描述',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '枚举值',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `data_type` varchar(64) NOT NULL DEFAULT 'STRING' COMMENT '数据类型',
  `depend_name` varchar(64) DEFAULT '' COMMENT '依赖字典名称',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为默认值选项',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_type` (`type`),
  KEY `index_dict_code` (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=170 DEFAULT CHARSET=utf8 COMMENT='通用数据字典';

DROP TABLE IF EXISTS `schedule_engine_job_cache`;
CREATE TABLE `schedule_engine_job_cache` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(256) NOT NULL COMMENT '任务id',
  `job_name` varchar(256) DEFAULT NULL COMMENT '任务名称',
  `compute_type` tinyint(2) NOT NULL COMMENT '计算类型stream/batch',
  `stage` tinyint(2) NOT NULL COMMENT '处于master等待队列：1 还是exe等待队列 2',
  `job_info` longtext NOT NULL COMMENT 'job信息',
  `node_address` varchar(256) DEFAULT NULL COMMENT '节点地址',
  `job_resource` varchar(256) DEFAULT NULL COMMENT 'job的计算引擎资源类型',
  `job_priority` bigint(20) DEFAULT NULL COMMENT '任务优先级',
  `is_failover` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0：不是，1：由故障恢复来的任务',
  `wait_reason` text COMMENT '任务等待原因',
  `tenant_id` int(11) DEFAULT NULL COMMENT '租户id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`(128))
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_engine_job_checkpoint`;
CREATE TABLE `schedule_engine_job_checkpoint` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(64) NOT NULL COMMENT '任务id',
  `task_engine_id` varchar(64) NOT NULL COMMENT '任务对于的引擎id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `checkpoint_id` int(11) DEFAULT '0' COMMENT '检查点id',
  `checkpoint_trigger` timestamp NULL DEFAULT NULL COMMENT 'checkpoint触发时间',
  `checkpoint_savepath` varchar(128) DEFAULT NULL COMMENT 'checkpoint存储路径',
  `checkpoint_counts` varchar(128) DEFAULT NULL COMMENT 'checkpoint信息中的counts指标',
  PRIMARY KEY (`id`),
  UNIQUE KEY `taskid_checkpoint` (`task_id`,`checkpoint_id`) COMMENT 'taskid和checkpoint组成的唯一索引',
  KEY `idx_task_engine_id` (`task_engine_id`) COMMENT '任务的引擎id'
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_engine_job_retry`;
CREATE TABLE `schedule_engine_job_retry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
  `job_id` varchar(256) NOT NULL COMMENT '离线任务id',
  `engine_job_id` varchar(256) DEFAULT NULL COMMENT '离线任务计算引擎id',
  `application_id` varchar(256) DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id',
  `exec_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `exec_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '执行时，重试的次数',
  `log_info` mediumtext COMMENT '错误信息',
  `engine_log` longtext COMMENT '引擎错误信息',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `retry_task_params` text COMMENT '重试任务参数',
  PRIMARY KEY (`id`),
  KEY `idx_job_id` (`job_id`) COMMENT '任务实例 id'
) ENGINE=InnoDB AUTO_INCREMENT=379 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_engine_unique_sign`;
CREATE TABLE `schedule_engine_unique_sign` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unique_sign` varchar(255) NOT NULL COMMENT '唯一标识',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_unique_sign` (`unique_sign`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_fill_data_job`;
CREATE TABLE `schedule_fill_data_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '补数据任务名称',
  `run_day` varchar(64) NOT NULL COMMENT '补数据运行日期yyyy-MM-dd',
  `from_day` varchar(64) DEFAULT NULL COMMENT '补数据开始业务日期yyyy-MM-dd',
  `to_day` varchar(64) DEFAULT NULL COMMENT '补数据结束业务日期yyyy-MM-dd',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` int(11) NOT NULL COMMENT '发起操作的用户',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `fill_data_info` mediumtext COMMENT '补数据信息',
  `fill_generate_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '补数据生成状态：0默认值，按照原来的接口逻辑走。1 表示正在生成，2 完成生成补数据实例，3生成补数据失败',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_task_id` (`tenant_id`,`job_name`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_job`;
CREATE TABLE `schedule_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `job_id` varchar(256) NOT NULL COMMENT '工作任务id',
  `job_key` varchar(256) NOT NULL DEFAULT '' COMMENT '工作任务key',
  `job_name` varchar(256) NOT NULL DEFAULT '' COMMENT '工作任务名称',
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` int(11) NOT NULL COMMENT '发起操作的用户',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '0正常调度 1补数据 2临时运行',
  `is_restart` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0：非重启任务, 1：重启任务',
  `cyc_time` varchar(64) NOT NULL COMMENT '调度时间 yyyyMMddHHmmss',
  `dependency_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '依赖类型',
  `flow_job_id` varchar(256) NOT NULL DEFAULT '0' COMMENT '工作流实例id',
  `period_type` tinyint(2) DEFAULT NULL COMMENT '周期类型',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
  `task_type` tinyint(1) NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL',
  `fill_id` int(11) DEFAULT '0' COMMENT '补数据id，默认为0',
  `exec_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `exec_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `exec_time` int(11) DEFAULT '0' COMMENT '执行时间',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `max_retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '最大重试次数',
  `retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '执行时，重试的次数',
  `node_address` varchar(64) DEFAULT NULL COMMENT '节点地址',
  `version_id` int(10) DEFAULT '0' COMMENT '任务运行时候版本号',
  `next_cyc_time` varchar(64) DEFAULT NULL COMMENT '下一次调度时间 yyyyMMddHHmmss',
  `engine_job_id` varchar(256) DEFAULT NULL COMMENT '离线任务计算引擎id',
  `application_id` varchar(256) DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id',
  `compute_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '计算类型STREAM(0), BATCH(1)',
  `phase_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '运行状态: CREATE(0):创建,JOIN_THE_TEAM(1):入队,LEAVE_THE_TEAM(2):出队',
  `job_execute_order` bigint(20) NOT NULL DEFAULT '0' COMMENT '按照计算时间排序字段',
  `fill_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0 默认值 周期实例，立即运行等非补数据实例的默认值 1 可执行补数据实例 2 中间实例 3 黑名单',
  `submit_user_name` varchar(64) DEFAULT NULL COMMENT '提交用户名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`(128),`is_deleted`),
  UNIQUE KEY `idx_jobKey` (`job_key`(255)),
  KEY `index_task_id` (`task_id`),
  KEY `index_fill_id` (`fill_id`),
  KEY `idx_name_type` (`job_name`(128),`type`),
  KEY `index_engine_job_id` (`engine_job_id`(128)),
  KEY `index_gmt_modified` (`gmt_modified`),
  KEY `idx_cyctime` (`cyc_time`),
  KEY `idx_exec_start_time` (`exec_start_time`),
  KEY `index_flow_job_id` (`flow_job_id`),
  KEY `index_job_execute_order` (`job_execute_order`)
) ENGINE=InnoDB AUTO_INCREMENT=7019 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_job_expand`;
CREATE TABLE `schedule_job_expand` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(256) NOT NULL COMMENT '工作任务id',
  `retry_task_params` mediumtext COMMENT '重试任务参数',
  `job_graph` mediumtext COMMENT 'jobGraph构建json',
  `job_extra_info` mediumtext COMMENT '任务提交额外信息',
  `engine_log` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `log_info` longtext COMMENT '错误信息',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`(128))
) ENGINE=InnoDB AUTO_INCREMENT=7018 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_job_graph_trigger`;
CREATE TABLE `schedule_job_graph_trigger` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trigger_type` tinyint(3) NOT NULL COMMENT '0:正常调度 1补数据',
  `trigger_time` datetime NOT NULL COMMENT '调度时间',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` int(10) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_trigger_time` (`trigger_time`)
) ENGINE=InnoDB AUTO_INCREMENT=195 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_job_job`;
CREATE TABLE `schedule_job_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `job_key` varchar(256) NOT NULL COMMENT 'batch 任务key',
  `parent_job_key` varchar(256) DEFAULT NULL COMMENT '对应batch任务父节点的key',
  `job_key_type` int(11) NOT NULL DEFAULT '2' COMMENT 'parentJobKey类型： RelyType 1. 自依赖实例key 2. 上游任务key 3. 上游任务的下一个周期key',
  `rule` int(11) DEFAULT NULL COMMENT 'parentJobKey类型： RelyType 1. 自依赖实例key 2. 上游任务key 3. 上游任务的下一个周期key',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_job_parentJobKey` (`job_key`(255),`parent_job_key`(255)),
  KEY `idx_job_jobKey` (`parent_job_key`(128)) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_job_operator_record`;
CREATE TABLE `schedule_job_operator_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(255) NOT NULL COMMENT '任务id',
  `version` int(10) DEFAULT '0' COMMENT '版本号',
  `operator_expired` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作过期时间',
  `operator_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '操作类型 0杀死 1重跑 2 补数据',
  `force_cancel_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '强制标志 0非强制 1强制',
  `node_address` varchar(255) DEFAULT NULL COMMENT '节点地址',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_id` (`job_id`,`operator_type`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_node_machine`;
CREATE TABLE `schedule_node_machine` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_plugin_info`;
CREATE TABLE `schedule_plugin_info` (
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

DROP TABLE IF EXISTS `schedule_plugin_job_info`;
CREATE TABLE `schedule_plugin_job_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(255) NOT NULL COMMENT '任务id',
  `job_info` longtext NOT NULL COMMENT '任务信息',
  `log_info` text COMMENT '任务信息',
  `status` tinyint(2) NOT NULL COMMENT '任务状态',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`(128)),
  KEY `idx_gmt_modified` (`gmt_modified`) COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_sql_text_temp`;
CREATE TABLE `schedule_sql_text_temp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(256) NOT NULL COMMENT '临时运行job的job_id',
  `sql_text` longtext NOT NULL COMMENT '临时运行任务的sql文本内容',
  `engine_type` varchar(64) NOT NULL COMMENT 'engineType类型',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8 COMMENT='临时任务sql_text关联表';

DROP TABLE IF EXISTS `schedule_task_shade`;
CREATE TABLE `schedule_task_shade` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL DEFAULT '-1' COMMENT '租户id',
  `name` varchar(256) NOT NULL DEFAULT '' COMMENT '任务名称',
  `task_type` tinyint(1) NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL',
  `compute_type` tinyint(1) NOT NULL COMMENT '计算类型 0实时，1 离线',
  `sql_text` longtext NOT NULL COMMENT 'sql 文本',
  `task_params` text NOT NULL COMMENT '任务参数',
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `schedule_conf` varchar(512) NOT NULL COMMENT '调度配置 json格式',
  `period_type` tinyint(2) DEFAULT NULL COMMENT '周期类型',
  `schedule_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0未开始,1正常调度,2暂停',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `modify_user_id` int(11) NOT NULL COMMENT '最后修改task的用户',
  `create_user_id` int(11) NOT NULL COMMENT '新建task的用户',
  `owner_user_id` int(11) NOT NULL COMMENT '任务负责人id',
  `version_id` int(11) NOT NULL DEFAULT '0' COMMENT 'task版本',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `task_desc` varchar(256) NOT NULL COMMENT '任务描述',
  `exe_args` text COMMENT '额外参数',
  `flow_id` int(11) NOT NULL DEFAULT '0' COMMENT '工作流id',
  `component_version` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_task_id` (`task_id`),
  KEY `index_name` (`name`(128))
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_task_shade_info`;
CREATE TABLE `schedule_task_shade_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `info` text COMMENT '任务运行信息',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_task_id` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedule_task_task_shade`;
CREATE TABLE `schedule_task_task_shade` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
  `parent_task_id` int(11) DEFAULT NULL COMMENT '对应batch任务父节点的id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_batch_task_task` (`task_id`,`parent_task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `task_param_template`;
CREATE TABLE `task_param_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_type` int(11) DEFAULT '0' COMMENT '任务类型',
  `task_name` varchar(20) DEFAULT NULL COMMENT '任务名称',
  `task_version` varchar(20) DEFAULT NULL COMMENT '任务版本',
  `params` text COMMENT '参数模版',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;