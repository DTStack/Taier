-- 删除两个没用的索引
ALTER TABLE `schedule_job`
DROP INDEX `index_project_id`,
DROP INDEX `index_status`;