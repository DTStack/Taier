-- 删除 flink on k8s 旧参数
delete from console_component_config where component_id = -102 and `key` IN( 'jarTmpDir', 'yarnAccepterTaskNumber', 'namespace');

alter table console_component add deploy_type tinyint(1) null comment '/* 0 standalone 1 yarn  */';

update console_component set deploy_type = 1 where component_type_code = 0;

update schedule_task_shade set component_version = null where app_type = 1 and component_version is not null and task_type = 2;

update console_component set is_deleted = 1 where component_type_code = 20;