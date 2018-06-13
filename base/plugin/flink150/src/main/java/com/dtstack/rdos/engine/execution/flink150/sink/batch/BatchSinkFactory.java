package com.dtstack.rdos.engine.execution.flink150.sink.batch;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink150.SqlPluginInfo;
import com.dtstack.rdos.engine.execution.base.loader.DtClassLoader;
import org.apache.flink.table.sinks.TableSink;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * 获得table sink
 * Date: 2017/7/25
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class BatchSinkFactory {

    public static String SINK_GENER_FUNC_NAME = "genBatchSink";

    /**jar包后缀*/
    public static String SUFFIX_JAR = "batchsink150";

    public static TableSink getTableSink(BatchCreateResultOperator resultOperator, SqlPluginInfo sqlPluginInfo) throws IOException,
            ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(!(classLoader instanceof DtClassLoader)){
            throw new RdosException("it's not a correct classLoader instance, it's type must be DtClassLoader!");
        }

        String resultType = resultOperator.getType();
        resultType += SUFFIX_JAR;

        String pluginJarPath = sqlPluginInfo.getJarFilePath(resultType);
        String className = sqlPluginInfo.getClassName(resultType);

        File pluginFile = new File(pluginJarPath);
        URL pluginJarURL = pluginFile.toURI().toURL();

        DtClassLoader dtClassLoader = (DtClassLoader) classLoader;
        dtClassLoader.addURL(pluginJarURL);
        Class<?> sinkClass = dtClassLoader.loadClass(className);
        for(Method method : sinkClass.getMethods()){
            if(method.getName().equals(SINK_GENER_FUNC_NAME)){
                Object object = sinkClass.newInstance();
                Object result = method.invoke(object, PublicUtil.ObjectToMap(resultOperator));
                return (TableSink) result;
            }
        }

        throw new RdosException("not support sink type:" + resultType + "!!!");
    }
}
