package com.dtstack.rdos.engine.execution.spark210;

import org.apache.spark.SparkConf;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.lang.reflect.Field;

/**
 * Created by sishu.yss on 2018/2/8.
 */
public class SparkStandAloneConfig {

    private static Logger logger = LoggerFactory.getLogger(SparkStandAloneConfig.class);

    /**默认每个处理器可以使用的内存大小*/
    private static final String SPARK_EXECUTOR_MEMORY = "512m";

    /**默认driver可以使用的内存大小*/
    private static final String SPARK_DRIVER_MEMORY = "512m";

    //executor 向driver 发送心跳的间隔时间
    private static final String SPARK_EXECUTOR_HEARTBEARTINTERVAL = "600s";

    //spark 所有网络传输的超时时间
    private static final String SPARK_NETWORK_TIMEOUT = "600s";

    //RPC 请求操作在超时前等待的持续时间
    private static final String SPARK_RPC_ASK_TIMEOUT = "600s";

    //如果设置为 "true" , 则执行任务的推测执行. 这意味着如果一个或多个任务在一个阶段中运行缓慢, 则将重新启动它们
    private static final String SPARK_SPECULATION = "true";

    //黑名单
    private static final String SPARK_BLACKLIST_ENABLED="true";

    //spark 为了本地数据最长的等待时间
    private static final String SPARK_LOCALITY_WAIT="10s";


    //    sparkConf.set("spark.executor.memory", DEFAULT_EXE_MEM);
//        sparkConf.set("spark.cores.max", DEFAULT_CORES_MAX);
//        sparkConf.set("spark.driver.supervise",DEFAULT_SUPERVISE);
//        sparkConf.set("spark.network.timeout",DEFAULT_NETWORK_TIMEOUT);
//        sparkConf.set("spark.rpc.askTimeout",DEFAULT_RPC_ASK_TIMEOUT);
//        sparkConf.set("spark.blacklist.enabled",DEFAULT_BLACKLIST_ENABLED);
//        sparkConf.set("spark.executor.heartbeatInterval",DEFAULT_EXECUTOR_HEARTBEARTINTERVAL);
//        sparkConf.set("spark.locality.wait",DEFAULT_LOCALITY_WAIT);
//        sparkConf.set("spark.speculation",DEFAULT_SPECULATION);
//        sparkConf.set("spark.driver.memory",DEFAULT_DRIVER_MEM);

    public static void initDefautlConf(SparkConf sparkConf){
        try{
            Field[] fields = SparkStandAloneConfig.class.getDeclaredFields();
            for(Field field:fields){
                String name = field.getName().replaceAll("_",".").toLowerCase();
                field.setAccessible(true);
                sparkConf.set(name,String.valueOf(field.get(SparkStandAloneConfig.class)));
            }
        }catch(Exception e){
            logger.error("",e);
        }

    }

    public static void main(String[] args) throws IllegalAccessException {
        initDefautlConf(null);
    }

}
