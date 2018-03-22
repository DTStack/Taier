package com.dtstack.rdos.engine.execution.sparkyarn;

import org.apache.spark.SparkConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Created by sishu.yss on 2018/3/9.
 */
public class SparkConfig {

    private static Logger logger = LoggerFactory.getLogger(SparkConfig.class);

    private static String spark_executor_memory = "512m";

    private static String spark_cores_max = "1";

    private static String spark_executor_instances = "1";

    private static String spark_executor_cores = "1";

    private static String spark_submit_deployMode="cluster";

    private static String spark_master="yarn";

    public static void initDefautlConf(SparkConf sparkConf){
        try{
            Field[] fields = SparkConfig.class.getDeclaredFields();
            for(Field field:fields){
                String name = field.getName().replaceAll("_",".");
                field.setAccessible(true);
                sparkConf.set(name,String.valueOf(field.get(SparkConfig.class)));
            }
        }catch(Exception e){
            logger.error("",e);
        }

    }
}
