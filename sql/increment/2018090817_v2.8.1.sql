-- engine 改造
alter table rdos_engine_job_cache add COLUMN node_address VARCHAR(256) DEFAULT NULL COMMENT '节点地址';

-- 队列管理
alter table rdos_engine_batch_job add COLUMN job_name VARCHAR(256) DEFAULT NULL COMMENT '任务名称';
alter table rdos_engine_stream_job add COLUMN task_name VARCHAR(256) DEFAULT NULL COMMENT '任务名称';