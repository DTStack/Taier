package com.dtstack.rdos.engine.execution.flink120.source;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.operator.stream.CreateSourceOperator;
import com.dtstack.rdos.engine.execution.flink120.util.PluginSourceUtil;
import com.dtstack.rdos.engine.execution.loader.DtClassLoader;
import org.apache.flink.table.sources.StreamTableSource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/3/10
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class StreamSourceFactory {

    public static String SINK_GENER_FUNC_NAME = "genStreamSource";

    public static String SUFFIX_JAR = "streamsource120";

    public static StreamTableSource getStreamSource(CreateSourceOperator sourceOperator) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {

        String sourceTypeStr = sourceOperator.getType();
        Properties properties = sourceOperator.getProperties();
        String[] fields = sourceOperator.getFields();
        Class<?>[] fieldTypes = sourceOperator.getFieldTypes();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(!(classLoader instanceof DtClassLoader)){
            throw new RdosException("it's not a correct classLoader instance, it's type must be DtClassLoader!");
        }

        sourceTypeStr += SUFFIX_JAR;
        String pluginJarPath = PluginSourceUtil.getJarFilePath(sourceTypeStr);
        String className = PluginSourceUtil.getClassName(sourceTypeStr);

        File pluginFile = new File(pluginJarPath);
        URL pluginJarURL = pluginFile.toURI().toURL();

        DtClassLoader dtClassLoader = (DtClassLoader) classLoader;
        dtClassLoader.addURL(pluginJarURL);
        Class<?> sinkClass = dtClassLoader.loadClass(className);
        for(Method method : sinkClass.getMethods()){
            if(method.getName().equals(SINK_GENER_FUNC_NAME)){
                Object object = sinkClass.newInstance();
                Object result = method.invoke(object, properties, fields, fieldTypes);
                return (StreamTableSource) result;
            }
        }

        throw new RdosException("not support for flink stream source type: " + sourceTypeStr);
    }
}
