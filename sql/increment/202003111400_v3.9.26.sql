ALTER TABLE  `rdos_engine_job_stop_record` modify  COLUMN `engine_type` varchar(256) DEFAULT NULL COMMENT '任务的执行引擎类型';
ALTER TABLE  `rdos_engine_job_stop_record` modify  COLUMN `compute_type` tinyint(2) DEFAULT NULL COMMENT '计算类型stream/batch';
delete from rdos_engine_job_stop_record where is_deleted = 1;
alter table rdos_engine_job_stop_record add `operator_expired` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作过期时间';