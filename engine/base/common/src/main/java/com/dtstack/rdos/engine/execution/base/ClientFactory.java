package com.dtstack.rdos.engine.execution.base;

import java.util.Map;

import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBack;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBackMethod;

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
        
    private static Map<String,IClient> pluginIClient = Maps.newConcurrentMap();
    
    private static Map<String,ClassLoader> pluginClassLoader = Maps.newConcurrentMap();

    private static ClassLoaderCallBackMethod classLoaderCallBackMethod = new ClassLoaderCallBackMethod();


    public static IClient getClient(String type) throws Exception{
    	type = type.toLowerCase();
    	IClient iClient = pluginIClient.get(type);
    	while(iClient == null){
    		initPluginClass(type,pluginClassLoader.get(type));
    		Thread.sleep(1000);
    		iClient = pluginIClient.get(type);
    		logger.warn("{}:initPluginClass again...",type);
    	}
        return iClient;
    }
    
	public static void initPluginClass(final String pluginType,
                                       ClassLoader classLoader) throws Exception{
    	pluginClassLoader.put(pluginType, classLoader);

        classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
            @Override
            public Object execute() throws Exception {
                switch (pluginType){
                    case "flink120":
                        pluginIClient.put(pluginType, (IClient) classLoader.loadClass("com.dtstack.rdos.engine.execution.flink120.FlinkClient").newInstance());
                        break;

                    case "flink130":
                        pluginIClient.put(pluginType, (IClient) classLoader.loadClass("com.dtstack.rdos.engine.execution.flink130.FlinkClient").newInstance());
                        break;

                    case "spark":
                        pluginIClient.put(pluginType, (IClient)classLoader.loadClass("com.dtstack.rdos.engine.execution.spark210.SparkClient").newInstance());
                        break;

                    case "datax":
                        pluginIClient.put(pluginType, (IClient)classLoader.loadClass("com.dtstack.rdos.engine.execution.datax.DataxClient").newInstance());
                        break;

                    case "spark_yarn":
                        pluginIClient.put(pluginType, (IClient)classLoader.loadClass("com.dtstack.rdos.engine.execution.sparkyarn.SparkYarnClient").newInstance());
                        break;

                    default:
                        throw new RuntimeException("not support for engine type " + pluginType);
                }
                return null;
            }
        },classLoader,null,true);
    }
}
