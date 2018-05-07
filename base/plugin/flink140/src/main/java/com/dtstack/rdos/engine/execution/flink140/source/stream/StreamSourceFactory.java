package com.dtstack.rdos.engine.execution.flink140.source.stream;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.operator.stream.CreateSourceOperator;
import com.dtstack.rdos.engine.execution.flink140.SqlPluginInfo;
import com.dtstack.rdos.engine.execution.base.loader.DtClassLoader;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;

/**
 * 创建streamTableSource
 * Date: 2017/3/10
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class StreamSourceFactory {

    public static String SOURCE_GENER_FUNC_NAME = "genStreamSource";

    private static String EVENT_TIME_KEY = "eventTime";

    private static String MAX_OUT_ORDERNESS_KEY = "maxOutOfOrderness";

    private static int DEFAULT_ORDERNESS_NUM = 10;

    /**jar包后缀*/
    public static String SUFFIX_JAR = "streamsource140";


    /**
     * 根据指定的类型构造数据源
     * 当前只支持kafka09
     * @param sourceOperator
     * @return
     */
    public static Table getStreamSource(CreateSourceOperator sourceOperator, StreamExecutionEnvironment env,
                                        StreamTableEnvironment tableEnv, SqlPluginInfo sqlPluginInfo) throws Exception {

        String sourceTypeStr = sourceOperator.getType();
        Properties properties = sourceOperator.getProperties();
        String[] fields = sourceOperator.getFields();
        Class<?>[] fieldTypes = sourceOperator.getFieldTypes();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(!(classLoader instanceof DtClassLoader)){
            throw new RdosException("it's not a correct classLoader instance, it's type must be DtClassLoader!");
        }

        sourceTypeStr += SUFFIX_JAR;
        String pluginJarPath = sqlPluginInfo.getJarFilePath(sourceTypeStr);
        String className = sqlPluginInfo.getClassName(sourceTypeStr);

        File pluginFile = new File(pluginJarPath);
        URL pluginJarURL = pluginFile.toURI().toURL();

        DtClassLoader dtClassLoader = (DtClassLoader) classLoader;
        dtClassLoader.addURL(pluginJarURL);
        Class<?> sourceClass = dtClassLoader.loadClass(className);
        for(Method method : sourceClass.getMethods()){
            if(method.getName().equals(SOURCE_GENER_FUNC_NAME)){
                Object object = sourceClass.newInstance();
                String eventTimeInfo = properties.getProperty(EVENT_TIME_KEY);
                int maxOutOfOrderness = DEFAULT_ORDERNESS_NUM;
                if(properties.contains(MAX_OUT_ORDERNESS_KEY)){
                    maxOutOfOrderness = MathUtil.getIntegerVal(properties.get(MAX_OUT_ORDERNESS_KEY));
                }

                Object[] extParam = new Object[]{env, tableEnv, eventTimeInfo, maxOutOfOrderness};

                return (Table) method.invoke(object, properties, fields, fieldTypes, extParam);
            }
        }

        throw new RdosException("not support for flink stream source type: " + sourceTypeStr);
    }

}
