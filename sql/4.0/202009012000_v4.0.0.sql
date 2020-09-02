ALTER TABLE `schedule_job` ADD `phase_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '运行状态: CREATE(0):创建,JOIN_THE_TEAM(1):入队,EXECUTE_OVER(2):执行完成';

