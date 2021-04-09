ALTER TABLE `schedule_task_task_shade` ADD  `task_key` varchar(128) NOT NULL DEFAULT '' COMMENT '任务的标识';
ALTER TABLE `schedule_task_task_shade` ADD  `parent_task_key` varchar(128) DEFAULT NULL COMMENT '父任务的标识';

CREATE INDEX index_task_key ON schedule_task_task_shade(`task_key`);
CREATE INDEX index_parent_task_key ON schedule_task_task_shade(`parent_task_key`);

UPDATE `schedule_task_task_shade` SET `task_key` = CONCAT(`task_id`,'-',`app_type`);
UPDATE `schedule_task_task_shade` SET `parent_task_key` = IF(`parent_task_id` IS NULL,NULL,CONCAT(`parent_task_id`,'-',`parent_app_type`));

