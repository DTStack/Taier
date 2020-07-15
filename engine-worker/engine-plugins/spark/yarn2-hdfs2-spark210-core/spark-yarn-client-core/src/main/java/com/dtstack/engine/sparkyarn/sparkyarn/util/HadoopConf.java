package com.dtstack.engine.sparkyarn.sparkyarn.util;


import com.dtstack.engine.base.util.HadoopConfTool;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;

/**
 *
 * @author sishu.yss
 *
 */
public class HadoopConf {

	private static final Logger LOG = LoggerFactory.getLogger(HadoopConf.class);

	private Configuration configuration;

	private Configuration yarnConfiguration;

    public HadoopConf(){
    }

    public void initHadoopConf(Map<String, Object> conf){

        configuration = new Configuration();
        HadoopConfTool.setFsHdfsImplDisableCache(configuration);
        conf.keySet().forEach(key ->{
            Object value = conf.get(key);
            if (value instanceof String){
                configuration.set(key, (String) value);
            } else if (value instanceof Boolean){
                configuration.setBoolean(key, (boolean) value);
            }
        });
    }

    public void initYarnConf(Map<String, Object> conf){

        yarnConfiguration = new YarnConfiguration(configuration);
        conf.keySet().forEach(key ->{
            Object value = conf.get(key);
            if (value instanceof String){
                yarnConfiguration.set(key, (String) value);
            } else if (value instanceof Boolean){
                yarnConfiguration.setBoolean(key, (boolean) value);
            }
        });

    }

    public void initHiveSecurityConf(Map<String, Object> conf){
        mapToConf(conf, yarnConfiguration);
    }

    public Configuration getConfiguration(){
		return configuration;
	}

	public String getDefaultFs(){
		return configuration.get("fs.defaultFS");
	}

	public Configuration getYarnConfiguration() {
		return yarnConfiguration;
	}

	private static void mapToConf(Map<String, Object> map, Configuration config){
        for (Map.Entry<String, Object> entry : map.entrySet()){
            if (entry.getValue() instanceof String){
                config.set(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Boolean){
                config.setBoolean(entry.getKey(), (Boolean) entry.getValue());
            } else if (entry.getValue() instanceof Map){
                mapToConf((Map<String, Object>) entry.getValue(), config);
            }
        }
    }
}
