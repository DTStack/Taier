ALTER TABLE schedule_task_shade ADD COLUMN `business_type` varchar(10) NULL  COMMENT '业务类型 应用自身定义';
ALTER TABLE schedule_job ADD COLUMN `business_type` varchar(10) NULL  COMMENT '业务类型 应用自身定义';
