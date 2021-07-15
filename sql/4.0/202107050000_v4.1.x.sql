ALTER TABLE `schedule_job` ADD COLUMN `job_execute_order` BIGINT NOT NULL DEFAULT 0 COMMENT '按照计算时间排序字段';

ALTER TABLE `schedule_job` ADD INDEX index_job_execute_order ( `job_execute_order` );
ALTER TABLE `schedule_job` ADD INDEX index_flow_job_id ( `flow_job_id` );

ALTER TABLE schedule_job_graph_trigger MODIFY `min_job_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成graph时对应的job起始id';

UPDATE schedule_job_graph_trigger SET min_job_id = CONCAT(SUBSTRING(FROM_UNIXTIME(UNIX_TIMESTAMP(trigger_time),'%Y%m%d%h%i'),3),'000000000');
