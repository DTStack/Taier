BEGIN;

INSERT INTO `rdos_user` VALUES ('-1', '-1', 'system@dtstack.com', 'system@dtstack.com', '0', '2017-06-05 20:35:16', '2017-06-05 20:35:16', '0', null, '111111');

INSERT INTO `rdos_role`(id, tenant_id, project_id, role_name, role_type, role_value, role_desc, modify_user_id, gmt_create, gmt_modified, is_deleted, create_user_id)
VALUES
  ('1', '-1', '-1', '租户所有者', '1', '1', '本项目内的全部权限，具有本租户内的最高权限', '0', now(), now(), '0', 0),
  ('8', '-1', '-1', '平台管理员', '1', '8', '本项目内的全部权限，具有本租户内的最高权限', '0', now(), now(), '0', 0),
  ('9', '-1', '-1', '租户管理员', '1', '9', '本项目内的全部权限，具有本租户内的最高权限', '0', now(), now(), '0', 0),
  ('2', '-1', '-1', '项目所有者', '1', '2', '项目空间的创建者，拥有项目空间内的最高权限，可对该项目空间的基本属性、数据源、当前项目空间的全部资源和项目成员等进行管理，并为项目成员赋予项目管理员、开发、运维、访客角色。', '0', now(), now(), '0', 0),
  ('3', '-1', '-1', '项目管理员', '1', '3', '项目空间的管理者，可对该项目空间的基本属性、数据源、当前项目空间的全部资源和项目成员等进行管理，并为项目成员赋予项目管理员、开发、运维、访客角色。项目管理员不能对项目所有者操作。', '0', now(), now(), '0', 0),
  ('6', '-1', '-1', '访客', '1', '4', '只具备查看权限，没有权限进行编辑任务、运维等操作。', '0', now(), now(), '0', 0),
  ('5', '-1', '-1', '运维', '1', '5', '由项目管理员分配运维权限；拥有发布任务、运维任务的操作权限，没有数据开发的操作权限。', '0', now(), now(), '0', 0),
  ('4', '-1', '-1', '数据开发', '1', '6', '能够创建任务、脚本、资源等，新建/删除表，但不能执行发布操作，不能管理数据源。', '0', now(), now(), '0', 0);

INSERT INTO `rdos_permission` VALUES (1,'root','root','root',0,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (2,'project','项目管理','项目管理',1,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (3,'project_edit','项目管理_创建/删除','创建/删除',2,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (4,'project_role','项目管理_角色管理','角色管理',2,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (5,'project_role_edit','项目管理_角色管理_编辑','编辑',4,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (6,'project_role_query','项目管理_角色管理_查看','查看',4,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (7,'project_member','项目管理_成员管理','成员管理',2,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (8,'project_member_edit','项目管理_成员管理_编辑','编辑',7,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (9,'project_member_query','项目管理_成员管理_查看','查看',7,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (10,'project_configure','项目管理_项目配置','项目配置',2,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (11,'project_configure_query','项目管理_项目配置_查看','查看',10,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (12,'project_configure_edit','项目管理_项目配置_编辑','编辑',10,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (13,'datadevelop','数据开发','数据开发',1,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (14,'datadevelop_batch','数据开发_离线任务','离线任务',13,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (15,'datadevelop_batch_resourcemanager','数据开发_离线任务_资源管理','资源管理',14,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (16,'datadevelop_batch_functionmanager','数据开发_离线任务_函数管理','函数管理',14,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (17,'datadevelop_batch_taskmanager','数据开发_离线任务_任务管理','任务管理',14,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (18,'datadevelop_batch_taskmanager_query','数据开发_离线任务_任务管理_查看','查看',17,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (19,'datadevelop_batch_taskmanager_edit','数据开发_离线任务_任务管理_编辑','编辑',17,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (20,'datadevelop_batch_taskmanager_publish','数据开发_离线任务_任务管理_发布/停止','发布/停止',17,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (21,'datadevelop_batch_scriptmanager','数据开发_离线任务_脚本管理','脚本管理',14,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (22,'datadevelop_batch_scriptmanager_query','数据开发_离线任务_脚本管理_查看','查看',21,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (23,'datadevelop_batch_scriptmanager_edit','数据开发_离线任务_脚本管理_编辑','编辑',21,1,now(),now(),0);


INSERT INTO `rdos_permission` VALUES (24,'batchintegration','数据集成','数据集成',1,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (25,'batchintegration_batch','数据集成_离线','离线',24,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (26,'batchintegration_batch_query','数据集成_离线_查看','查看',24,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (27,'batchintegration_batch_edit','数据集成_离线_引入数据源','引入数据源',24,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (28,'datamanager','数据管理','数据管理',1,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (29,'datamanager_tablemanager','数据管理_表管理','表管理',28,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (30,'datamanager_tablemanager_edit','数据管理_表管理_编辑','编辑',29,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (31,'datamanager_tablemanager_query','数据管理_表管理_查看','查看',29,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (32,'datamanager_tablemanager_editcharge','数据管理_表管理_编辑负责人','编辑负责人',29,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (33,'datamanager_handlerecord','数据管理_操作记录','操作记录',28,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (34,'datamanager_catalogue','数据管理_数据类目','数据类目',28,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (35,'datamanager_catalogue_query','数据管理_数据类目_查看','查看',34,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (36,'datamanager_catalogue_edit','数据管理_数据类目_编辑','编辑',34,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (37,'datamanager_dirtydata','数据管理_脏数据管理','脏数据管理',28,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (38,'datamanager_permissionmanager','数据管理_权限管理','权限管理',28,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (39,'datamanager_permissionmanager_query','数据管理_权限管理_查看','查看',38,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (40,'datamanager_permissionmanager_edit','数据管理_权限管理_编辑','编辑',38,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (41,'datamanager_permissionmanager_apply','数据管理_权限管理_申请','申请',38,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (42,'maintenance','运维中心','运维中心',1,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (43,'maintenance_pandect','运维管理_总览','总览',42,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (44,'maintenance_pandect_batch','运维管理_总览_离线','离线',43,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (45,'maintenance_batch','运维中心_离线任务运维','离线任务运维',42,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (46,'maintenance_batch_query','运维中心_离线任务运维_查看','查看',45,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (47,'maintenance_batch_taskop','运维中心_离线任务运维_任务控制','任务控制',45,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (48,'maintenance_batchtaskmanager','运维中心_离线任务管理','离线任务管理',42,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (49,'maintenance_batchtaskmanager_filldata','运维中心_离线任务管理_补数据','补数据',48,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (50,'maintenance_batchtaskmanager_query','运维中心_离线任务管理_查看','查看',48,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (51,'maintenance_alarm','运维中心_监控告警','监控告警',42,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (52,'maintenance_alarm_custom','运维中心_监控告警_自定义告警','自定义告警',51,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (53,'maintenance_alarm_custom_batch','运维中心_监控告警_自定义告警_离线','离线',52,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (54,'maintenance_alarm_custom_batch_edit','运维中心_监控告警_自定义告警_离线_编辑','编辑',53,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (55,'maintenance_alarm_custom_batch_query','运维中心_监控告警_自定义告警_离线_查看','查看',53,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (56,'maintenance_alarm_record','运维中心_监控告警_告警记录','告警记录',51,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (57,'maintenance_alarm_record_batch','运维中心_监控告警_告警记录_离线','离线',56,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (58,'datamodel_manager','数据模型','数据模型',1,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (59,'datamodel_manager_query','数据模型_查看','查看',58,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (60,'datamodel_manager_edit','数据模型_编辑','编辑',58,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (61,'test_produce','生产测试项目','生产测试项目',1,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (62,'test_produce_binding_project','生产测试项目_项目绑定','项目绑定',61,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (63,'test_produce_edit_schedule_status','生产测试项目_编辑调度状态','编辑调度状态',61,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (64,'test_produce_edit_package','生产测试项目_编辑包','编辑包',61,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (65,'test_produce_query_package','生产测试项目_查看包','查看包',61,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (66,'test_produce_publish_package','生产测试项目_发布包','发布包',61,1,now(),now(),0);


INSERT INTO `rdos_permission` VALUES (67,'datamask','数据脱敏','数据脱敏',1,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (68,'datamask_rule_edit','数据脱敏_规则编辑','规则编辑',67,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (69,'datamask_rule_query','数据脱敏_规则查看','规则查看',67,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (70,'datamask_config_edit','数据脱敏_配置编辑','配置编辑',67,1,now(),now(),0);
INSERT INTO `rdos_permission` VALUES (71,'datamask_config_query','数据脱敏_配置查看','配置查看',67,1,now(),now(),0);

INSERT INTO `rdos_permission` VALUES (72,'batchintegration_batch_dbsync','数据集成_整库同步','整库同步',24,1,now(),now(),0);

commit;