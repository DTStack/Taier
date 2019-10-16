package com.dtstack.rdos.engine.execution.base;

import java.util.Map;

import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBack;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBackMethod;

import com.google.common.collect.Maps;

/**
 * TODO 修改---根据不同的配置生成不同的client实例
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ClientFactory {

    private static Map<String, ClassLoader> pluginClassLoader = Maps.newConcurrentMap();

    private static Map<String, String> typeRefClassName = Maps.newHashMap();

    static {
        typeRefClassName.put("flink150", "com.dtstack.rdos.engine.execution.flink150.FlinkClient");
        typeRefClassName.put("flink170", "com.dtstack.rdos.engine.execution.flink170.FlinkClient");
        typeRefClassName.put("flink180", "com.dtstack.rdos.engine.execution.flink180.FlinkClient");
        typeRefClassName.put("spark", "com.dtstack.rdos.engine.execution.spark210.SparkClient");
        typeRefClassName.put("datax", "com.dtstack.rdos.engine.execution.datax.DataxClient");
        typeRefClassName.put("spark_yarn", "com.dtstack.rdos.engine.execution.sparkyarn.SparkYarnClient");
        typeRefClassName.put("spark_yarn_cdh", "com.dtstack.rdos.engine.execution.spark160.sparkyarn.SparkYarnClient");
        typeRefClassName.put("mysql", "com.dtstack.rdos.engine.execution.mysql.MysqlClient");
        typeRefClassName.put("postgresql", "com.dtstack.rdos.engine.execution.postgresql.PostgreSQLClient");
        typeRefClassName.put("oracle", "com.dtstack.rdos.engine.execution.oracle.OracleClient");
        typeRefClassName.put("sqlserver", "com.dtstack.rdos.engine.execution.sqlserver.SqlserverClient");
        typeRefClassName.put("maxcompute", "com.dtstack.rdos.engine.execution.odps.OdpsClient");
        typeRefClassName.put("hadoop", "com.dtstack.rdos.engine.execution.hadoop.HadoopClient");
        typeRefClassName.put("hive", "com.dtstack.rdos.engine.execution.hive.HiveClient");
        typeRefClassName.put("learning", "com.dtstack.rdos.engine.execution.learning.LearningClient");
        typeRefClassName.put("dtyarnshell", "com.dtstack.rdos.engine.execution.yarnshell.DtYarnShellClient");
        typeRefClassName.put("kylin", "com.dtstack.rdos.engine.execution.kylin.KylinClient");
        typeRefClassName.put("impala", "com.dtstack.rdos.engine.execution.impala.ImpalaClient");
    }

    public static ClassLoader getClassLoader(String pluginType){
        return pluginClassLoader.get(pluginType);
    }
    
	public static IClient createPluginClass(String pluginType) throws Exception{

        ClassLoader classLoader = pluginClassLoader.get(pluginType);
        return ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<IClient>(){

            @Override
            public IClient execute() throws Exception {
                String className = typeRefClassName.get(pluginType);
                if(className == null){
                    throw new RuntimeException("not support for engine type " + pluginType);
                }

                IClient client = classLoader.loadClass(className).asSubclass(IClient.class).newInstance();
                return new ClientProxy(client);
            }
        }, classLoader, true);
    }

    public static void addClassLoader(String pluginType, ClassLoader classLoader){
        if(pluginClassLoader.containsKey(pluginType)){
            return;
        }

        pluginClassLoader.putIfAbsent(pluginType, classLoader);
    }

    public static boolean checkContainClassLoader(String pluginType){
        return pluginClassLoader.containsKey(pluginType);
    }
}
