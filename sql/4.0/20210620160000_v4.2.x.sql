-- 删除 flink on k8s 旧参数
delete from console_component_config where component_id = -102 and `key` IN( 'jarTmpDir', 'yarnAccepterTaskNumber', 'namespace');