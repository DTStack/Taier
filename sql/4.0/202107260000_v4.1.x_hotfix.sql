update console_component_config SET `key` = 'jobmanager.heap.mb'
where `key` = 'jobmanager.memory.mb' and dependencyKey = 'deploymode$session'
  and component_type_code = 0
  AND component_id in (SELECT id from console_component where component_type_code = 0 and hadoop_version = '110');

update console_component_config SET `key` = 'jobmanager.heap.mb'
where `key` = 'jobmanager.memory.mb' and dependencyKey = 'deploymode$session'
  and component_type_code = 0
  AND component_id = -109;


update console_component_config SET `key` = 'taskmanager.heap.mb'
where `key` = 'taskmanager.memory.mb' and dependencyKey = 'deploymode$session'
  and component_type_code = 0
  AND component_id in (SELECT id from console_component where component_type_code = 0 and hadoop_version = '110');

update console_component_config SET `key` = 'taskmanager.heap.mb'
where `key` = 'taskmanager.memory.mb' and dependencyKey = 'deploymode$session'
  and component_type_code = 0
  AND component_id  = -109;
