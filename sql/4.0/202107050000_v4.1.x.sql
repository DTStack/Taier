ALTER TABLE `schedule_job` ADD COLUMN `job_execute_order` BIGINT NOT NULL DEFAULT 0 COMMENT '按照计算时间排序字段';

ALTER TABLE `schedule_job` ADD INDEX index_job_execute_order ( `job_execute_order` );