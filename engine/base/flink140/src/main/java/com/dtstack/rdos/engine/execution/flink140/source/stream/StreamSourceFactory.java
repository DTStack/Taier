package com.dtstack.rdos.engine.execution.flink140.source.stream;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.operator.stream.CreateSourceOperator;
import com.dtstack.rdos.engine.execution.flink140.util.PluginSourceUtil;
import com.dtstack.rdos.engine.execution.loader.DtClassLoader;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

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
                                        StreamTableEnvironment tableEnv) throws Exception {

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
        Class<?> sourceClass = dtClassLoader.loadClass(className);
        for(Method method : sourceClass.getMethods()){
            if(method.getName().equals(SOURCE_GENER_FUNC_NAME)){
                Object object = sourceClass.newInstance();
                Object[] extParam = new Object[]{env};
                Object result = method.invoke(object, properties, fields, fieldTypes, extParam);

                return assignWaterMarker((DataStream<Row>) result, sourceOperator, tableEnv);
            }
        }

        throw new RdosException("not support for flink stream source type: " + sourceTypeStr);
    }

    public static Table assignWaterMarker(DataStream<Row> dataStream, CreateSourceOperator sourceOperator, StreamTableEnvironment tableEnv){

        Properties properties = sourceOperator.getProperties();
        String eventTimeInfo = properties.getProperty(EVENT_TIME_KEY);
        String[] fieldArr = sourceOperator.getFields();
        String fields = StringUtils.join(fieldArr, ",");

        if(Strings.isNullOrEmpty(eventTimeInfo)){
            return tableEnv.fromDataStream(dataStream, fields);
        }

        fields = fields + ",rowtime.rowtime";
        String[] infoArr = eventTimeInfo.split(":");
        Preconditions.checkState(infoArr.length >= 2, " illegal property of eventTime.");
        String fieldName = infoArr[0];
        String fieldType = infoArr[1];

        int pos = -1;
        for(int i=0; i<fieldArr.length; i++){
            if(fieldName.equals(fieldArr[i])){
                pos = i;
            }
        }

        int maxOutOfOrderness = DEFAULT_ORDERNESS_NUM;
        if(properties.contains(MAX_OUT_ORDERNESS_KEY)){
            maxOutOfOrderness = MathUtil.getIntegerVal(properties.get(MAX_OUT_ORDERNESS_KEY));
        }

        BoundedOutOfOrdernessTimestampExtractor waterMarker = null;
        if(fieldType.equalsIgnoreCase("string")){

            Preconditions.checkState(pos != -1, "can not find specified eventTime field:" + fieldName + " in defined fields.");
            Preconditions.checkState(infoArr.length >= 3, "illegal property: eventTime");
            waterMarker = new CustomerWaterMarkerForString(Time.seconds(maxOutOfOrderness), pos, infoArr[2]);
        }else if(fieldType.equalsIgnoreCase("long")){

            waterMarker = new CustomerWaterMarkerForLong(Time.seconds(maxOutOfOrderness), pos);
        }else{

            throw new IllegalArgumentException("not support type of " + fieldType + ", current only support(string, long).");
        }

        dataStream = dataStream.assignTimestampsAndWatermarks(waterMarker);
        return tableEnv.fromDataStream(dataStream, fields);
    }

}
