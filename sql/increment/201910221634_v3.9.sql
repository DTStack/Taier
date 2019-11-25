-- 数据拷贝
insert into rdos_engine_batch_job_retry (status,job_id,engine_job_id,application_id,exec_start_time,exec_end_time,retry_num,log_info,engine_log,gmt_create,gmt_modified,is_deleted) select status,task_id,engine_task_id,application_id,exec_start_time,exec_end_time,retry_num,log_info,engine_log,gmt_create,gmt_modified,is_deleted from rdos_engine_stream_job_retry s where s.is_deleted = 0;
-- 重命名
rename table rdos_engine_batch_job_retry to rdos_engine_job_retry;

-- 增加索引
ALTER TABLE `rdos_engine_batch_job` ADD INDEX index_gmt_modified (`gmt_modified` );
-- 新增字段compute_type
alter table rdos_engine_batch_job add compute_type tinyint(1) NOT NULL DEFAULT '1' COMMENT '计算类型STREAM(0), BATCH(1)';
-- 数据拷贝
insert into rdos_engine_batch_job (status,job_id,engine_job_id,application_id,job_name,exec_start_time,exec_end_time,exec_time,retry_num,log_info,engine_log,plugin_info_id,gmt_create,gmt_modified,is_deleted,compute_type) select status,task_id,engine_task_id,application_id,task_name,exec_start_time,exec_end_time,exec_time,retry_num,log_info,engine_log,plugin_info_id,gmt_create,gmt_modified,is_deleted,0 from rdos_engine_stream_job s where s.is_deleted = 0;
-- 重命名
rename table rdos_engine_batch_job to rdos_engine_job;