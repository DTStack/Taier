package com.dtstack.engine.hadoop.util;


import org.apache.hadoop.conf.Configuration;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @author sishu.yss
 *
 */
public class HadoopConf {

    private Configuration configuration;

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

    public Configuration getConfiguration(){
        return configuration;
    }

    public String getDefaultFs(){
        return configuration.get("fs.defaultFS");
    }
}
