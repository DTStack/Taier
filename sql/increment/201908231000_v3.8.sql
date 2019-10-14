ALTER TABLE  `rdos_engine_batch_job_retry` modify  COLUMN `retry_task_params`  text;
ALTER TABLE  `rdos_engine_batch_job` modify  COLUMN `retry_task_params`  text;
