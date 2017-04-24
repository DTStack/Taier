package com.dtstack.rdos.engine.execution.base;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class ClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(ClientFactory.class);
    
    private static Map<String,ClassLoader> pluginClassLoaders =  Maps.newConcurrentMap();

    public static IClient getClient(String type) throws Exception{

        IClient client = null;
        type = type.toLowerCase();

        switch (type){
            case "flink":
            	client = (IClient) pluginClassLoaders.get(type).loadClass("com.dtstack.rdos.engine.execution.flink120.FlinkClient").newInstance();
                break;
            case "spark":
            	client = (IClient) pluginClassLoaders.get(type).loadClass("com.dtstack.rdos.engine.execution.spark210.SparkClient").newInstance();
                break;
                
                default:
                    logger.error("not support for engine type " + type);
                    break;
        }

        return client;
    }
    
    public static void setPluginClassLoaders(String pluginType,ClassLoader classLoader){
    	pluginClassLoaders.put(pluginType, classLoader);
    }
}
