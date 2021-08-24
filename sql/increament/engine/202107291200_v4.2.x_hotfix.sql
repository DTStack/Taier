INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)

select cluster_id, component_id, 2,'INPUT', 0, 'hadoop.username', 'admin', null, 'yarn', null, null, now(),
       now(), 0 from console_component_config where component_type_code = 2 and cluster_id != -2 and is_deleted = 0
                                                and component_id not in(select component_id from console_component_config where component_type_code = 2 and cluster_id != -2 and `key` = 'hadoop.username')
group by component_id;
