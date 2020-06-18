package com.dtstack.engine.sparkyarn.sparkyarn.utils;

import com.dtstack.engine.sparkyarn.sparkyarn.config.SparkK8sConfig;
import com.google.common.collect.Maps;
import org.apache.spark.SparkConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by sishu.yss on 2018/3/9.
 */
public class SparkConfigUtil {

    private static Logger logger = LoggerFactory.getLogger(SparkConfigUtil.class);

    private static final String KEY_PRE_STR = "spark.";

    private static final String DEFAULT_FILE_FORMAT = "orc";

    private static final String SESSION_CONF_KEY_PREFIX = "session.";

    private static final String KEY_DEFAULT_FILE_FORMAT = "hive.default.fileformat";

    private static String spark_executor_memory = "512m";

    private static String spark_cores_max = "1";

    private static String spark_executor_instances = "1";

    private static String spark_executor_cores = "1";

    private static String spark_submit_deployMode = "cluster";

    private static String spark_master = "yarn";

    //executor 向driver 发送心跳的间隔时间
    private static final String SPARK_EXECUTOR_HEARTBEARTINTERVAL = "600s";

    //spark 所有网络传输的超时时间
    private static final String SPARK_NETWORK_TIMEOUT = "600s";

    //rpc 请求操作在超时前等待的持续时间
    private static final String SPARK_RPC_ASK_TIMEOUT = "600s";

    //如果设置为 "true" , 则执行任务的推测执行. 这意味着如果一个或多个任务在一个阶段中运行缓慢, 则将重新启动它们
    private static final String SPARK_SPECULATION = "true";

    private static final String SPARK_YARN_MAX_APP_ATTEMPTS = "1";

    public static void initDefautlConf(SparkConf sparkConf) {
        try {
            Field[] fields = SparkConfigUtil.class.getDeclaredFields();
            for (Field field : fields) {
                String name = field.getName().replaceAll("_", ".");
                field.setAccessible(true);
                sparkConf.set(name, String.valueOf(field.get(SparkConfigUtil.class)));
            }
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    public static SparkConf buildBasicSparkConf(Properties sparkDefaultProp) {
        SparkConf sparkConf = new SparkConf();

        if (sparkDefaultProp != null) {
            sparkDefaultProp.stringPropertyNames()
                    .stream()
                    .filter(key -> key.toString().contains("."))
                    .forEach(key -> sparkConf.set(key, sparkDefaultProp.getProperty(key).toString()));
        }

        SparkConfigUtil.initDefautlConf(sparkConf);
        return sparkConf;
    }

    public static void replaceBasicSparkConf(SparkConf sparkConf, Properties confProperties) {
        if (!Objects.isNull(confProperties)) {
            for (Map.Entry<Object, Object> param : confProperties.entrySet()) {
                String key = (String) param.getKey();
                String val = (String) param.getValue();
                if (!key.contains(KEY_PRE_STR)) {
                    key = KEY_PRE_STR + key;
                }
                sparkConf.set(key, val);
            }
        }
    }


    public static void buildHadoopSparkConf(SparkConf sparkConf,SparkK8sConfig sparkK8sConfig ){
        sparkConf.set("hive-site.xml", getCoreSiteContent(sparkK8sConfig.getHiveConf()));
        sparkConf.set("hdfs-site.xml", getCoreSiteContent(sparkK8sConfig.getHadoopConf()));
    }


    protected static String getCoreSiteContent(Map hadoopConfMap) {
        StringBuilder hadoopConfContent = new StringBuilder();
        hadoopConfContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(System.lineSeparator());
        hadoopConfContent.append("<?xml-stylesheet href=\"configuration.xsl\" type=\"text/xsl\"?>").append(System.lineSeparator());
        hadoopConfContent.append("<configuration>").append(System.lineSeparator());
        Iterator<Map.Entry<String, Object>> it = hadoopConfMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> e = it.next();
            String name = e.getKey();
            String value = e.getValue().toString();
            hadoopConfContent.append("    <property>").append(System.lineSeparator());
            hadoopConfContent.append("        <name>").append(name).append("</name>").append(System.lineSeparator());
            hadoopConfContent.append("        <value>").append(value).append("</value>").append(System.lineSeparator());
            hadoopConfContent.append("    </property>").append(System.lineSeparator());
        }
        hadoopConfContent.append("</configuration>").append(System.lineSeparator());

        return hadoopConfContent.toString();
    }


    public static Map<String, String> getSparkSessionConf(Properties confProp) {
        Map<String, String> map = Maps.newHashMap();
        map.put(KEY_DEFAULT_FILE_FORMAT, DEFAULT_FILE_FORMAT);

        confProp.stringPropertyNames()
                .stream()
                .filter(key -> key.startsWith(SESSION_CONF_KEY_PREFIX))
                .forEach(key -> {
                    String value = confProp.getProperty(key);
                    key = key.replaceFirst("session\\.", "");
                    map.put(key, value);
                });

        return map;
    }
}
