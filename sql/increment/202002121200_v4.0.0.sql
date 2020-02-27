
-- 新增字段
alter table rdos_engine_job_cache change `group_name `job`_resource` varchar(256)  DEFAULT '' COMMENT '计算引擎类型';
alter table rdos_engine_job_stop_record change `group_name `job`_resource` varchar(256)  DEFAULT '' COMMENT '计算引擎类型';





