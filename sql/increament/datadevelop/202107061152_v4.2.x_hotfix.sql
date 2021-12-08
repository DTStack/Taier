-- 伪删除数据源连通性定时任务
update dt_center_cron_schedule set is_deleted = 1, gmt_modified = now() where job_detail_name = 'batchDataSourceJob';
-- 删除定时任务关联表  防止 有些定时任务 置为伪删除 但是相关表记录还存在 导致日志中不断报错
delete from qrtz_cron_triggers;
delete from qrtz_triggers;
delete from qrtz_job_details ;
