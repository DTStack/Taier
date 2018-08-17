alter table rdos_engine_job_cache modify column job_info longtext NOT NULL COMMENT 'job信息';
alter table rdos_plugin_job_info modify column job_info LONGTEXT NOT NULL COMMENT '任务信息';