alter table schedule_job add job_extra_info mediumtext null comment '任务提交额外信息';
update schedule_job set job_extra_info = JSON_OBJECT('job_graph',job_graph) where is_deleted = 0 and job_graph is not null;
