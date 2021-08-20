ALTER TABLE `schedule_fill_data_job`
    MODIFY COLUMN `job_name` varchar(256) NOT NULL DEFAULT '' COMMENT '补数据任务名称';