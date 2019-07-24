ALTER TABLE `rdos_engine_batch_job` ADD COLUMN `retry_task_params` varchar(256);
ALTER TABLE `rdos_engine_batch_job_retry` ADD COLUMN `retry_task_params` varchar(256);
