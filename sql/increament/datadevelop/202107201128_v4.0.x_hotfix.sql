ALTER TABLE`rdos_batch_job_alarm`
MODIFY COLUMN `job_id` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'engine job_id' AFTER `id`;