update console_component_config set `value` = '10s' where
        `key` = 'spark.executor.heartbeatInterval' and `value` = '600s' and component_type_code = 1;