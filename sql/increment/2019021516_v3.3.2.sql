alter table rdos_engine_batch_job add column retry_num INT(5) DEFAULT 0;

alter table rdos_engine_stream_job add column retry_num INT(5) DEFAULT 0;