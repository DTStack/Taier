package com.dtstack.rdos.engine.execution.flink150.source.batch;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchCreateSourceOperator;
import com.dtstack.rdos.engine.execution.flink150.SqlPluginInfo;
import com.dtstack.rdos.engine.execution.base.loader.DtClassLoader;
import org.apache.flink.table.sources.BatchTableSource;

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
 * @ahthor xuchao
 */

public class BatchSourceFactory {

    public static String SOURCE_GENER_FUNC_NAME = "genBatchSource";

    /**jar包后缀*/
    public static String SUFFIX_JAR = "batchsource150";

    /**
     * 根据指定的类型构造数据源
     * 当前只支持kafka09
     * @param sourceOperator
     * @return
     */
    public static BatchTableSource getBatchSource(BatchCreateSourceOperator sourceOperator, SqlPluginInfo sqlPluginInfo) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {

        Properties properties = sourceOperator.getProperties();
        String sourceTypeStr = sourceOperator.getType();
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
        Class<?> sinkClass = dtClassLoader.loadClass(className);
        for(Method method : sinkClass.getMethods()){
            if(method.getName().equals(SOURCE_GENER_FUNC_NAME)){
                Object object = sinkClass.newInstance();
                Object result = method.invoke(object, properties, fields, fieldTypes);
                return (BatchTableSource) result;
            }
        }

        throw new RdosException("not support for flink batch source type: " + sourceTypeStr);
    }

}
