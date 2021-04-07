ALTER TABLE `schedule_task_task_shade` ADD  `task_key` varchar(128) NOT NULL DEFAULT '' COMMENT '任务的标识';
ALTER TABLE `schedule_task_task_shade` ADD  `parent_task_key` varchar(128) NOT NULL DEFAULT '' COMMENT '父任务的标识';


UPDATE `schedule_task_task_shade` SET `task_key` = CONCAT(`task_id`,'-',`app_type`);
UPDATE `schedule_task_task_shade` SET `parent_task_key` = IF(`parent_task_id` IS NULL,'',CONCAT(`parent_task_id`,'-',`parent_app_type`));
