ALTER TABLE `schedule_job` ADD COLUMN `job_execute_order` BIGINT NOT NULL DEFAULT 0 COMMENT '按照计算时间排序字段';

ALTER TABLE `schedule_job` ADD INDEX index_job_execute_order ( `job_execute_order` );

ALTER TABLE schedule_job_graph_trigger MODIFY `min_job_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成graph时对应的job起始id';

UPDATE schedule_job_graph_trigger SET min_job_id = CONCAT(SUBSTRING(FROM_UNIXTIME(UNIX_TIMESTAMP(trigger_time),'%Y%m%d%H%i'),3),'000000000');

UPDATE schedule_job set job_execute_order = CONCAT(SUBSTRING(cyc_time,3,LENGTH(cyc_time)-4),RIGHT(CONCAT('000000000', id),9))
WHERE cyc_time > FROM_UNIXTIME(UNIX_TIMESTAMP(CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY),'%Y%m%d%H%i%s')
and cyc_time <  FROM_UNIXTIME(UNIX_TIMESTAMP(DATE_SUB( DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY),INTERVAL 1 SECOND)),'%Y%m%d%H%i%s');
