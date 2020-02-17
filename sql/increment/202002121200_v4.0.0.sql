
-- 新增字段
-- alter table rdos_engine_job_cache add job_resource varchar(256) NOT NULL DEFAULT '' COMMENT '计算引擎类型';

alter table rdos_engine_job_cache add COLUMN is_failover tinyint(1) NOT NULL DEFAULT '0' COMMENT '0：不是，1：由故障恢复来的任务';

