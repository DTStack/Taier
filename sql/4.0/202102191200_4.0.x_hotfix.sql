drop procedure if exists create_retry_jobId;
delimiter $$
CREATE PROCEDURE `create_retry_jobId`()
begin
    if not exists (SELECT 1
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'dagschedulex' AND TABLE_NAME='schedule_engine_job_retry' AND
    INDEX_NAME='idx_job_id') then
        Alter Table schedule_engine_job_retry ADD INDEX `idx_job_id`(`job_id`) COMMENT 'jobId';
    end if;
end$$
delimiter ;
CALL create_retry_jobId();
drop procedure if exists create_retry_jobId;