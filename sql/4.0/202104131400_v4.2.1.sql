ALTER TABLE `schedule_job`
ADD COLUMN `sql_text` longtext DEFAULT NULL COMMENT '临时运行sql文本内容' AFTER `task_rule`;
