-- engine 改造
alter table rdos_engine_job_cache add COLUMN node_address VARCHAR(256) DEFAULT NULL COMMENT '节点地址';

-- 队列管理
alter table rdos_engine_batch_job add COLUMN job_name VARCHAR(256) DEFAULT NULL COMMENT '任务名称';
alter table rdos_engine_stream_job add COLUMN task_name VARCHAR(256) DEFAULT NULL COMMENT '任务名称';


UPDATE rdos_engine_job_cache d, (select a.stage,b.`status`,b.job_id,b.engine_job_id from rdos_engine_job_cache a left join rdos_engine_batch_job b 
on a.job_id=b.job_id
where a.stage=2 and b.engine_job_id is null and status in (16,17)) c 
SET d.stage=1
WHERE d.job_id = c.job_id