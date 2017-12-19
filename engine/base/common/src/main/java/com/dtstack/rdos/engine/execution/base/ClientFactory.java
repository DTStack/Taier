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
 * @author xuchao
 */

public class ClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(ClientFactory.class);
        
    private static Map<String, IClient> pluginIClient = Maps.newConcurrentMap();
    
    private static Map<String, ClassLoader> pluginClassLoader = Maps.newConcurrentMap();

    private static ClassLoaderCallBackMethod classLoaderCallBackMethod = new ClassLoaderCallBackMethod();

    private static Map<String, String> typeRefClassName = Maps.newHashMap();

    static{
        typeRefClassName.put("flink120", "com.dtstack.rdos.engine.execution.flink120.FlinkClient");
        typeRefClassName.put("flink130", "com.dtstack.rdos.engine.execution.flink130.FlinkClient");
        typeRefClassName.put("flink140", "com.dtstack.rdos.engine.execution.flink140.FlinkClient");
        typeRefClassName.put("spark", "com.dtstack.rdos.engine.execution.spark210.SparkClient");
        typeRefClassName.put("datax", "com.dtstack.rdos.engine.execution.datax.DataxClient");
        typeRefClassName.put("spark_yarn", "com.dtstack.rdos.engine.execution.sparkyarn.SparkYarnClient");
    }


    public static IClient getClient(String type) throws Exception{
    	type = type.toLowerCase();
    	IClient iClient = pluginIClient.get(type);
    	while(iClient == null){
    		initPluginClass(type, pluginClassLoader.get(type));
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

                String className = typeRefClassName.get(pluginType);
                if(className == null){
                    throw new RuntimeException("not support for engine type " + pluginType);
                }

                IClient client = classLoader.loadClass(className).asSubclass(IClient.class).newInstance();
                ClientProxy proxyClient = new ClientProxy(client);
                pluginIClient.put(pluginType, proxyClient);
                return null;
            }
        },classLoader,null,true);
    }
}
