update schedule_engine_job_checkpoint set checkpoint_id = 0 where checkpoint_id = '';
alter table schedule_engine_job_checkpoint modify checkpoint_id int default 0 null comment '检查点id';