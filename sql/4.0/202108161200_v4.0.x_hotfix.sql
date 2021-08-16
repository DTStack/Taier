update console_component_config set `value` = '/data/miniconda3/bin/python3' where
        `key` = 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON' and `value` = '/data/miniconda2/bin/python3' and component_type_code = 1;

update console_component_config set `value` = '/data/miniconda3/bin/python3' where
        `key` = 'spark.yarn.appMasterEnv.PYSPARK_PYTHON' and `value` = '/data/miniconda2/bin/python3' and component_type_code = 1;