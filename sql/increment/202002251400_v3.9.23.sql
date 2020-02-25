delete from rdos_engine_job_stop_record where is_deleted = 1;
alter table rdos_engine_job_stop_record add `operator_expired` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作过期时间';