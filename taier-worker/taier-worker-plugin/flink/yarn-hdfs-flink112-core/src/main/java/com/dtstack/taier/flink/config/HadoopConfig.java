package com.dtstack.taier.flink.config;

import com.dtstack.taier.base.util.HadoopConfTool;
import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * todo: add add-configuration method
 * @author sishu.yss
 *
 */
public class HadoopConfig {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopConfig.class);

    private Configuration coreConfiguration;
	private YarnConfiguration yarnConfiguration;

	private HadoopConfig(){}

    public HadoopConfig(Map<String, Object> coreConf, Map<String, Object> yarnConf){
        initCoreConfig(coreConf);
        initYarnConfig(yarnConf);
    }

    private void initCoreConfig(Map<String, Object> conf){

        coreConfiguration = new Configuration(false);
        HadoopConfTool.setFsHdfsImplDisableCache(coreConfiguration);
        // todo: support all type conversion
        if(MapUtils.isNotEmpty(conf)){
            conf.keySet().forEach(key ->{
                Object value = conf.get(key);
                if (value instanceof String){
                    coreConfiguration.set(key, (String) value);
                } else if (value instanceof Boolean){
                    coreConfiguration.setBoolean(key, (boolean) value);
                }
            });
            conf.put(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);
        }

        coreConfiguration.setBoolean(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);
    }

    private void initYarnConfig(Map<String, Object> conf){

        yarnConfiguration = new YarnConfiguration(coreConfiguration);
        // todo: support all type conversion
        if(MapUtils.isNotEmpty(conf)){
            conf.keySet().forEach(key ->{
                Object value = conf.get(key);
                if (value instanceof String){
                    yarnConfiguration.set(key, (String) value);
                } else if (value instanceof Boolean){
                    yarnConfiguration.setBoolean(key, (boolean) value);
                }
            });
            conf.put(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);
        }

        HadoopConfTool.setDefaultYarnConf(yarnConfiguration, conf);
    }

    public Configuration getCoreConfiguration(){
        return coreConfiguration;
    }

    public String getDefaultFs(){
        return coreConfiguration.get("fs.defaultFS");
    }

    public YarnConfiguration getYarnConfiguration() {
        return yarnConfiguration;
    }

}
