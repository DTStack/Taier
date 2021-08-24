ALTER TABLE console_security_log ADD is_deleted  tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除';

ALTER TABLE alert_record CHANGE gmt_created gmt_create timestamp default CURRENT_TIMESTAMP not null;

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('data_clear_name', 'schedule_job', '{"deleteDateConfig":30,"clearDateConfig":180}', null, 8, 1, 'STRING', '', 0, '2021-07-05 11:22:36', '2021-07-05 11:22:36', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('data_clear_name', 'schedule_job_job', '{"deleteDateConfig":30,"clearDateConfig":180}', null, 8, 1, 'STRING', '', 0, '2021-07-05 11:22:36', '2021-07-05 11:22:36', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('data_clear_name', 'schedule_fill_data_job', '{"deleteDateConfig":30,"clearDateConfig":180}', null, 8, 1, 'STRING', '', 0, '2021-07-05 11:22:36', '2021-07-05 11:22:36', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('data_clear_name', 'schedule_engine_unique_sign', '{"directDelete":true,"deleteDateConfig":270}', null, 8, 1, 'STRING', '', 0, '2021-07-05 11:22:36', '2021-07-05 11:22:36', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('data_clear_name', 'schedule_plugin_job_info', '{"deleteDateConfig":30,"clearDateConfig":60}', null, 8, 1, 'STRING', '', 0, '2021-07-05 11:22:36', '2021-07-05 11:22:36', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('data_clear_name', 'console_security_log', '{"deleteDateConfig":30,"clearDateConfig":60}', null, 8, 1, 'STRING', '', 0, '2021-07-05 11:22:36', '2021-07-05 11:22:36', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('data_clear_name', 'alert_record', '{"deleteDateConfig":30,"clearDateConfig":60}', null, 8, 1, 'STRING', '', 0, '2021-07-05 11:22:36', '2021-07-05 11:22:36', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('data_clear_name', 'alert_content', '{"deleteDateConfig":30,"clearDateConfig":60}', null, 8, 1, 'STRING', '', 0, '2021-07-05 11:22:36', '2021-07-05 11:22:36', 0);