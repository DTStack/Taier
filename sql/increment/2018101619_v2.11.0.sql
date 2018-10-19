-- 队列管理
alter table rdos_engine_job_cache add COLUMN job_name VARCHAR(256) DEFAULT NULL COMMENT '任务名称';
