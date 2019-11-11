package com.dtstack.engine.dtscript.execution.spark210;

import org.apache.spark.SparkConf;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.lang.reflect.Field;

/**
 * Created by sishu.yss on 2018/2/8.
 */
public class SparkConfig {

    private static Logger logger = LoggerFactory.getLogger(SparkConfig.class);

    /**默认每个处理器可以使用的内存大小*/
    private static final String spark_executor_memory = "512m";

    /**默认driver可以使用的内存大小*/
    private static final String spark_driver_memory = "512m";

    //executor 向driver 发送心跳的间隔时间
    private static final String spark_executor_heartbeartinterval = "600s";

    //spark 所有网络传输的超时时间
    private static final String spark_network_timeout = "600s";

    //rpc 请求操作在超时前等待的持续时间
    private static final String spark_rpc_ask_timeout = "600s";

    //如果设置为 "true" , 则执行任务的推测执行. 这意味着如果一个或多个任务在一个阶段中运行缓慢, 则将重新启动它们
    private static final String spark_speculation = "true";

    //黑名单
    private static final String spark_blacklist_enabled="false";

    //spark 为了本地数据最长的等待时间
    private static final String spark_locality_wait="10s";

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

    public static void main(String[] args) throws IllegalAccessException {
        initDefautlConf(null);
    }

}
