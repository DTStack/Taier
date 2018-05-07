package com.dtstack.rdos.engine.execution.flink140.sink.stream;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.operator.stream.StreamCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink140.SqlPluginInfo;
import com.dtstack.rdos.engine.execution.base.loader.DtClassLoader;
import org.apache.flink.table.sinks.TableSink;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * 根据指定的sink type 加载jar,并初始化对象
 * Date: 2017/3/10
 * Company: www.dtstack.com
 * @author xuchao
 */

public class StreamSinkFactory {

    public static String SINK_GENER_FUNC_NAME = "genStreamSink";

    /**jar包后缀*/
    public static String SUFFIX_JAR = "streamsink140";

    public static TableSink getTableSink(StreamCreateResultOperator resultOperator) throws IOException,
            ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(!(classLoader instanceof DtClassLoader)){
            throw new RdosException("it's not a correct classLoader instance, it's type must be DtClassLoader!");
        }

        String resultType = resultOperator.getType();
        resultType += SUFFIX_JAR;

        String pluginJarPath = SqlPluginInfo.getJarFilePath(resultType);
        String className = SqlPluginInfo.getClassName(resultType);

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
