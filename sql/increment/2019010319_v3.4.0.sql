alter table `rdos_engine_batch_job` add COLUMN(`application_id` varchar(256) DEFAULT NULL);
alter table `rdos_stream_task_checkpoint` add unique key `ck_unique` (`task_id`,`task_engine_id`);