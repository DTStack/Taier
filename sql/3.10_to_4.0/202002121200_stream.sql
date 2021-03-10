-- 更新流计算的任务的状态 不执行sql 流计算的状态3.10升级到4.0  状态会是等待提交
-- 不执行此sql 在界面上面手动提交一下 结果一样的
insert into schedule_job(tenant_id, project_id, dtuic_tenant_id, app_type, job_id, job_key, job_name,
                         task_id, gmt_create, gmt_modified, create_user_id, is_deleted, type, is_restart,
                         business_date, cyc_time, dependency_type, flow_job_id, period_type, status,
                         task_type, fill_id, exec_start_time, exec_end_time, exec_time, submit_time,
                         max_retry_num, retry_num, log_info, node_address, version_id, next_cyc_time,
                         engine_job_id, application_id, engine_log, plugin_info_id, source_type,
                         retry_task_params, compute_type)
SELECT sj.tenant_id,
       sj.project_id,
       rt.dtuic_tenant_id,
       7,
       sj.task_id,
       CONCAT('tempJob', sj.task_id, DATE_FORMAT(sj.gmt_create, '%Y%m%d')),
       IFNULL(ej.job_name,st.name),
       -1,
       sj.gmt_create,
       sj.gmt_modified,
       st.create_user_id,
       st.is_deleted,
       2,
       0,
       '',
       '',
       0,
       0,
       null,
       IFNULL(ej.status, 13),
       case st.task_type
           when 11 then 2
           when 0 then 0
           when 1 then 1
           else st.task_type
           end,
       0,
       ej.exec_start_time,
       ej.exec_end_time,
       ej.exec_time,
       null,
       0,
       0,
       sj.log_info,
       '',
       st.version,
       null,
       ej.engine_job_id,
       ej.application_id,
       ej.engine_log,
       ej.plugin_info_id,
       ej.source_type,
       ej.retry_task_params,
       0
FROM streamapp.rdos_stream_job sj
         LEFT JOIN streamapp.rdos_stream_task st ON sj.task_id = st.task_id
         LEFT JOIN ide.rdos_engine_job ej on ej.job_id = sj.task_id
         LEFT JOIN streamapp.rdos_tenant rt on sj.tenant_id = rt.id
where sj.is_deleted = 0
  and st.is_deleted = 0;