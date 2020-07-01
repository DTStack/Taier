ALTER TABLE `schedule_job` DROP COLUMN `source_type`;
ALTER TABLE `schedule_job_job` ADD INDEX `idx_job_jobKey`(`parent_job_key`(128)) USING BTREE;