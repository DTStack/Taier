alter table schedule_job add job_extra_info text null comment '任务提交额外信息';
update schedule_job set job_extra_info = JSON_OBJECT('job_graph',job_graph) where is_deleted = 0 and job_graph is not null;
alter table schedule_job drop column job_graph;
