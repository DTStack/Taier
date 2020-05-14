ALTER TABLE schedule_engine_job_checkpoint ADD INDEX idx_task_engine_id (`task_engine_id`) COMMENT '任务的引擎id';
ALTER TABLE schedule_plugin_job_info ADD INDEX idx_gmt_modified (`gmt_modified`) COMMENT '修改时间';
ALTER TABLE schedule_engine_job_retry ADD INDEX idx_job_id (`job_id`) COMMENT '任务实例 id';