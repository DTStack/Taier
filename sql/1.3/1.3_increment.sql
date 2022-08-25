-- 任务和集群队列绑定
alter table develop_task add queue_name varchar(64) default  null comment 'yarn队列名称';

alter table schedule_task_shade add queue_name varchar(64) default  null comment 'yarn队列名称';

