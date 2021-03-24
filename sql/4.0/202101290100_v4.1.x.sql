CREATE TABLE `alert_content` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT '租户id',
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `app_type` int(11) NOT NULL COMMENT '应用类型，1：RDOS, 2:数据质量, 3:数据API ,4: 标签工程 ,5:数据地图',
  `content` text NOT NULL COMMENT '内容文本',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '触发类型',
  `send_info` varchar(2055) NOT NULL DEFAULT '' COMMENT '发送信息(上下文对象)',
  `alert_message_status` tinyint(3) NOT NULL DEFAULT '0' COMMENT '告警状态: 0 以告警 1 未告警',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='告警消息记录表';

CREATE TABLE `alert_record` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `alert_channel_id` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '通道id',
  `alert_gate_type` smallint(2) DEFAULT '0' COMMENT '通道类型 SMS(1,"短信"),MAIL(2,"邮箱") , DINGDING(3,"钉钉"),CUSTOMIZE(4,"自定义")',
  `alert_content_id` int(11) NOT NULL DEFAULT '0' COMMENT '消息记录id',
  `tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT '租户id',
  `app_type` int(11) NOT NULL DEFAULT '0' COMMENT '应用类型，1：RDOS, 2:数据质量, 3:数据API ,4: 标签工程 ,5:数据地图',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '接收人id',
  `read_id` int(11) NOT NULL DEFAULT '0' COMMENT '应用记录id',
  `read_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未读 1:已读',
  `title` varchar(32) NOT NULL DEFAULT '' COMMENT '标题',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '触发类型',
  `context` longtext NOT NULL COMMENT '上下文对象',
  `job_id` varchar(256) NOT NULL DEFAULT '' COMMENT '工作任务id',
  `alert_record_status` tinyint(3) NOT NULL DEFAULT '0' COMMENT '告警状态: 0 未告警 1 告警队列中 2 告警发送中 3 告警成功 4 待扫描中',
  `alert_record_send_status` tinyint(3) NOT NULL DEFAULT '0' COMMENT '告警发送状态: 0 未发送 1 发送成功 2 发送失败',
  `failure_reason` varchar(2055) NOT NULL DEFAULT '' COMMENT '当alert_send_status状态是2时，才会有值',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `node_address` varchar(256) NOT NULL COMMENT '节点地址',
  `send_time` varchar(256) NOT NULL DEFAULT '' COMMENT '发送时间 yyyyMMddHHmmss',
  `send_end_time` varchar(256) NOT NULL DEFAULT '' COMMENT '发送结束时间 yyyyMMddHHmmss',
  `gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_user_id` (`user_id`),
  KEY `index_job_id` (`job_id`),
  KEY `index_select` (`tenant_id`,`user_id`,`app_type`),
  KEY `index_gmt_created` (`gmt_created`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='告警发送记录表';

CREATE TABLE `alert_channel` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `cluster_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后一次操作通道的租户id,',
  `alert_gate_name` varchar(32) DEFAULT '' COMMENT '通道名称',
  `alert_gate_type` smallint(2) DEFAULT '0' COMMENT '通道类型 SMS(1,"短信"),MAIL(2,"邮箱") , DINGDING(3,"钉钉"),CUSTOMIZE(4,"自定义")',
  `alert_gate_code` varchar(16) DEFAULT '' COMMENT '通道运行编号代码:sms_yp,sms_dy,sms_API,mail_dt,mail_api,ding_dt,ding_api,sms_jar,mail_jar,ding_jar,phone_tc,custom_jar(具体看代码中枚举AlertGateCode)',
  `alert_gate_json` varchar(1024) DEFAULT '' COMMENT '通道配置信息',
  `alert_gate_source` varchar(32) DEFAULT '' COMMENT '通道标识',
  `alert_template` text COMMENT '通道模板',
  `file_path` varchar(255) DEFAULT '' COMMENT '自定义jar存储位置',
  `is_default` tinyint(3) NOT NULL DEFAULT '0' COMMENT '是否是默认通道: 0 非默认 1 默认通道',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  `gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_alert_gate_source` (`alert_gate_source`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='告警通道';

-- 迁移 通道表
INSERT  IGNORE INTO `alert_channel` ( `id`, `cluster_id`, `alert_gate_name`, `alert_gate_type`, `alert_gate_code`, `alert_gate_json`, `alert_gate_source`, `file_path`, `is_default`, `is_deleted`, `gmt_created`, `gmt_modified`,alert_template )
SELECT
g.id,
a.cluster_id,
g.alert_gate_name,
g.alert_gate_type,
g.alert_gate_code,
g.alert_gate_json,
g.alert_gate_source,
g.file_path,
a.is_default,
g.is_deleted,
g.gmt_created,
g.gmt_modified,
(select t.alert_template from dt_alert_template t WHERE g.alert_gate_source = t.alert_gate_source AND g.is_deleted = 0 AND t.is_deleted = 0 LIMIT 1 ) alert_template
FROM
	`dt_alert_gate` g,
	`dt_cluster_alert` a
WHERE
	g.id = a.alert_id AND g.is_deleted = 0;

-- 迁移内容表
INSERT IGNORE INTO `alert_content` (`id`,`tenant_id`,`project_id`,`app_type`,`content`,`status`,`gmt_create`,`gmt_modified`,`is_deleted`)
SELECT * FROM dt_notify_record_content;

-- 迁移记录表
INSERT IGNORE INTO `alert_record`(`id`,`alert_content_id`,`tenant_id`,`app_type`,`user_id`,`read_status`,`status`,`context`,`alert_record_status`,`alert_record_send_status`,`is_deleted`,`node_address`,`read_id`,`gmt_created`,`gmt_modified`)
SELECT
r.id,
r.content_id alert_channel_id ,
r.tenant_id,
r.app_type,
r.user_id,
r.read_status,
0,
CONCAT("{\"content\":\"",(SELECT c.content FROM dt_notify_record_content c WHERE r.content_id = c.id LIMIT 1),"\"}" )  context,
3,
1,
r.is_deleted,
"",
r.notify_record_id,
r.gmt_create,
r.gmt_modified
FROM dt_notify_record_read r
WHERE r.is_deleted = 0;