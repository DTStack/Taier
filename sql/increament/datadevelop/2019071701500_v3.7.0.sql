ALTER TABLE rdos_engine_batch_job
ADD version_id int null comment '任务对应版本id';

alter table `rdos_hive_table_info` MODIFY COLUMN `table_type` TINYINT(2) NOT NULL COMMENT '表类型';
