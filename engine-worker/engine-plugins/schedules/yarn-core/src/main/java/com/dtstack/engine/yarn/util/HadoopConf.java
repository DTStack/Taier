package com.dtstack.engine.yarn.util;


import com.dtstack.engine.base.util.HadoopConfTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @author sishu.yss
 *
 */
public class HadoopConf {

    private Configuration configuration;

    private Configuration yarnConfiguration;

    public HadoopConf(){

    }

    public void initHadoopConf(Map<String, Object> conf){
        if(null == conf){
            return;
        }

        configuration = new Configuration();

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
        if(Objects.isNull(conf)){
            return;
        }

        yarnConfiguration = configuration == null ? new YarnConfiguration() : new YarnConfiguration(configuration);

        conf.keySet().forEach(key ->{
            Object value = conf.get(key);
            if (value instanceof String){
                yarnConfiguration.set(key, (String) value);
            } else if (value instanceof Boolean){
                yarnConfiguration.setBoolean(key, (boolean) value);
            }
        });
        HadoopConfTool.setDefaultYarnConf(yarnConfiguration, conf);
    }

    public Configuration getConfiguration(){
        return configuration;
    }

    public Configuration getYarnConfiguration() {
        return yarnConfiguration;
    }

    public String getDefaultFs(){
        return configuration.get("fs.defaultFS");
    }
}
