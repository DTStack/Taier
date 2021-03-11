-- task数据迁移 需要迁移task的数据时 才执行此sql
insert IGNORE into schedule_task_shade( tenant_id, project_id, dtuic_tenant_id, app_type, node_pid, name, task_type, engine_type, compute_type, sql_text, task_params, task_id, schedule_conf, period_type, schedule_status, project_schedule_status, submit_status, gmt_create, gmt_modified, modify_user_id, create_user_id, owner_user_id, version_id, is_deleted, task_desc, main_class, exe_args, flow_id, is_publish_to_produce, extra_info, is_expire)
select
    ts.tenant_id,
    ts.project_id,
    ts.dtuic_tenant_id,
    ts.app_type,
    ts.node_pid,
    ts.name,
    ts.task_type,
    ts.engine_type,
    ts.compute_type,
    ts.sql_text,
    ts.task_params,
    ts.task_id,
    ts.schedule_conf,
    ts.period_type,
    ts.schedule_status,
    ts.project_schedule_status,
    ts.submit_status,
    ts.gmt_create,
    ts.gmt_modified,
    ts.modify_user_id,
    ts.create_user_id,
    ts.owner_user_id,
    ts.version_id,
    ts.is_deleted,
    ts.task_desc,
    ts.main_class,
    ts.exe_args,
    ts.flow_id,
    ts.is_publish_to_produce,
    ts.extra_info,
    ts.is_expire
from task.rdos_batch_task_shade ts;



insert IGNORE into schedule_task_task_shade (tenant_id, project_id, dtuic_tenant_id, app_type, task_id, parent_task_id,
                                             gmt_create, gmt_modified, is_deleted)
select ts.tenant_id,
       ts.project_id,
       ts.dtuic_tenant_id,
       ts.app_type,
       ts.task_id,
       ts.parent_task_id,
       ts.gmt_create,
       ts.gmt_modified,
       ts.is_deleted
from task.rdos_batch_task_task_shade ts;




insert IGNORE into schedule_job_job (tenant_id, project_id, dtuic_tenant_id, app_type, job_key, parent_job_key,
                                     gmt_create, gmt_modified, is_deleted)
select jj.tenant_id,
       jj.project_id,
       jj.dtuic_tenant_id,
       jj.app_type,
       jj.job_key,
       jj.parent_job_key,
       jj.gmt_create,
       jj.gmt_modified,
       jj.is_deleted
from task.rdos_batch_job_job jj;


INSERT IGNORE INTO schedule_job(`tenant_id`, `project_id`, `dtuic_tenant_id`, `app_type`, `job_id`, `job_key`, `job_name`, `task_id`, `gmt_create`, `gmt_modified`, `create_user_id`,
                                `is_deleted`, `type`, `is_restart`, `business_date`, `cyc_time`, `dependency_type`,`flow_job_id`, `period_type`, `status`, `task_type`, `fill_id`, `exec_start_time`, `exec_end_time`,
                                `exec_time`, `submit_time`, `max_retry_num`, `retry_num`, `log_info`, `node_address`, `version_id`, `next_cyc_time`, `engine_job_id`, `application_id`, `engine_log`, `plugin_info_id`,
                                `source_type`, `retry_task_params`, `compute_type`)
select bj.tenant_id,
       bj.project_id,
       bj.dtuic_tenant_id,
       bj.app_type,
       bj.job_id,
       bj.job_key,
       bj.job_name,
       bj.task_id,
       bj.gmt_create,
       bj.gmt_modified,
       bj.create_user_id,
       bj.is_deleted,
       bj.type,
       bj.is_restart,
       bj.business_date,
       bj.cyc_time,
       bj.dependency_type,
       bj.flow_job_id,
       bj.period_type,
       IFNULL(rej.status,bj.status),
       bj.task_type,
       bj.fill_id,
       bj.exec_start_time,
       bj.exec_end_time,
       bj.exec_time,
       bj.submit_time,
       bj.max_retry_num,
       bj.retry_num,
       rej.log_info,
       bj.node_address,
       rej.version_id,
       bj.next_cyc_time,
       rej.engine_job_id,
       rej.application_id,
       rej.engine_log,
       rej.plugin_info_id,
       rej.source_type,
       rej.retry_task_params,
       IFNULL(rej.compute_type,1)
from task.rdos_batch_job bj LEFT JOIN ide.rdos_engine_job rej on bj.job_id = rej.job_id;
