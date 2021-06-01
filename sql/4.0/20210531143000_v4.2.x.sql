update schedule_dict set dict_name = '2.1'  where dict_code = 'spark_version' and dict_value = 210;
update schedule_dict set dict_name = '2.4'  where dict_code = 'spark_version' and dict_value = 240;
alter table schedule_job drop column component_version;
update schedule_task_shade set component_version = null where is_deleted = 0;