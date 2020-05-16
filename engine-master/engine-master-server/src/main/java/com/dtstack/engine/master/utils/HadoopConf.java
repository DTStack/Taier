package com.dtstack.engine.master.utils;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.schedule.common.kerberos.KerberosConfigVerify;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author sishu.yss
 */
public class HadoopConf {

    private static Logger logger = LoggerFactory.getLogger(HadoopConf.class);

    private static ClusterService clusterService;

    private final static String HADOOP_CONFIGE = System.getProperty("user.dir") + "/conf/hadoop/";

    private final static String HADOOP_CONF_DIR = System.getenv("HADOOP_CONF_DIR");

    private static Configuration defaultConfiguration = new Configuration(false);
    private static YarnConfiguration defaultYarnConfiguration = new YarnConfiguration();

    private static volatile Map<Long, Object> parallelLockMap = Maps.newConcurrentMap();

    private static volatile Map<Long, Configuration> configurationMap = Maps.newConcurrentMap();

    private static volatile Map<Long, Configuration> yarnConfigurationMap = Maps.newConcurrentMap();;

    private static final String FS_HDFS_IMPL_DISABLE_CACHE ="fs.hdfs.impl.disable.cache";
    private static final String IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED ="ipc.client.fallback-to-simple-auth-allowed";

    private static final List<String> HDFS_PRE = Arrays.asList("dfs", "fs", "hadoop");
    private static final List<String> YARN_PRE = Arrays.asList("yarn", "hadoop.security.authentication", "hadoop.security.authorization");


    static {
        try {
            String dir = StringUtils.isNotBlank(HADOOP_CONF_DIR) ? HADOOP_CONF_DIR : HADOOP_CONFIGE;
//            defaultConfiguration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
//            defaultConfiguration.set("fs.hdfs.impl.disable.cache", "true");
            File[] xmlFileList = new File(dir).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".xml")) {
                        return true;
                    }
                    return false;
                }
            });

            if (xmlFileList != null) {
                for (File xmlFile : xmlFileList) {
                    defaultConfiguration.addResource(xmlFile.toURI().toURL());
                }
            }
            defaultYarnConfiguration = new YarnConfiguration(defaultConfiguration);
        } catch (Exception e) {
            logger.error("{}", e);
        }
    }

    private static Object getConfigurationLock(Long lockKey) {
        Object lock = HadoopConf.class;
        if (parallelLockMap != null) {
            Object newLock = new Object();
            lock = parallelLockMap.putIfAbsent(lockKey, newLock);
            if (lock == null) {
                lock = newLock;
            }
        }
        return lock;
    }

    /**
     * 获取hdfs和yarn的总和
     *
     * @param dtuicTenantId
     * @return
     */
    public static Configuration getFullConfiguration(long dtuicTenantId) {
        Configuration hadoopConf = getConfiguration(dtuicTenantId);
        YarnConfiguration yarnConfiguration = getYarnConfiguration(dtuicTenantId);

        for (Map.Entry<String, String> entry : yarnConfiguration) {
            hadoopConf.set(entry.getKey(), entry.getValue());
        }
        return hadoopConf;
    }

    public static Configuration getConfiguration(long dtuicTenantId) {
        Configuration configuration = configurationMap.get(dtuicTenantId);
        if (configuration == null) {
            synchronized (getConfigurationLock(dtuicTenantId)) {
                if (configurationMap.get(dtuicTenantId) == null) {
                    try {
                        Map<String, Object> hadoopConf = clusterService.getConfig(dtuicTenantId, EComponentType.HDFS.getConfName());
                        if (MapUtils.isNotEmpty(hadoopConf)) {
                            configuration = initHadoopConf(hadoopConf);
                        }
                    } catch (Exception e) {
                        logger.error("{}", e);
                    }
                    if (configuration == null) {
                        configuration = defaultConfiguration;
                    }
                    configurationMap.put(dtuicTenantId, configuration);
                }
            }
        }

        return configuration;
    }


    public static YarnConfiguration getYarnConfiguration(long dtuicTenantId) {
        Configuration yarnConf = yarnConfigurationMap.get(dtuicTenantId);
        if(yarnConf == null){
            synchronized (yarnConfigurationMap){
                if( yarnConfigurationMap.get(dtuicTenantId) == null){

                    try{
                        Configuration configuration = getConfiguration(dtuicTenantId);
                        yarnConf = new YarnConfiguration(configuration);

                        Map<String, Object> yarnConfMap = clusterService.getConfig(dtuicTenantId, EComponentType.YARN.getConfName());
                        initYarnConfiguration((YarnConfiguration)yarnConf, yarnConfMap);

                        yarnConfigurationMap.put(dtuicTenantId, yarnConf);
                    }catch (Exception e){
                        logger.error("{}", e);
                        throw new RuntimeException("", e);
                    }
                }
            }
        }

        return (YarnConfiguration) yarnConf;
    }



    public static Configuration initHadoopConf(Map<String, Object> conf) {
        KerberosConfigVerify.replaceHost(conf);

        if (conf == null || conf.size() == 0) {
            //读取环境变量--走默认配置
            return defaultConfiguration;
        }

        Configuration configuration = new Configuration(false);
        setDefaultConf(configuration);

        for (String key : conf.keySet()) {
            configuration.set(key, conf.get(key).toString());
        }
        return configuration;
    }

    public Configuration getHadoopConf(Map<String, Object> conf) {
        KerberosConfigVerify.replaceHost(conf);

        Configuration configuration = new Configuration(false);
        setDefaultConf(configuration);

        for (String key : conf.keySet()) {
            configuration.set(key, conf.get(key).toString());
        }
        return configuration;
    }

    public static JSONObject getHadoopConfJSONObject(Map<String, Object> conf) {

        JSONObject hadoopConf = new JSONObject();
        for (Map.Entry<String, Object> keyVal : conf.entrySet()) {
            if (checkKeyUseful(keyVal.getKey(), keyVal.getValue(), HDFS_PRE)) {
                hadoopConf.put(keyVal.getKey(), keyVal.getValue().toString());
            }
        }
        //hdfs以下配置固定
        hadoopConf.put("fs.hdfs.impl.disable.cache",true);
        hadoopConf.put("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem");
        return hadoopConf;
    }

    private static void initYarnConfiguration(YarnConfiguration yarnConfiguration, Map<String, Object> map){
        for(Map.Entry<String, Object> entry : map.entrySet()){
            yarnConfiguration.set(entry.getKey(), entry.getValue().toString());
        }
        setDefaultConf(yarnConfiguration);
    }

    public YarnConfiguration getYarnConf(Map<String, Object> conf) {
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        for (Map.Entry<String, Object> keyVal : conf.entrySet()) {
            if (checkKeyUseful(keyVal.getKey(), keyVal.getValue(), YARN_PRE)) {
                yarnConfiguration.set(keyVal.getKey(), keyVal.getValue().toString());
            }
        }
        return yarnConfiguration;
    }

    public static JSONObject getYarnConfJSONObject(Map<String, Object> conf) {

        JSONObject yarnConf = new JSONObject();

        for (Map.Entry<String, Object> keyVal : conf.entrySet()) {
            if (checkKeyUseful(keyVal.getKey(), keyVal.getValue(), YARN_PRE)) {
                yarnConf.put(keyVal.getKey(), keyVal.getValue().toString());
            }
        }
        return yarnConf;
    }

    private static boolean checkKeyUseful(String key, Object value, List<String> keySufList) {
        for (String suf : keySufList) {
            if (key.startsWith(suf) && value != null) {
                return true;
            }
        }
        return false;
    }

    public static String getDefaultFs(Long dtuicTenantId) {
        return getConfiguration(dtuicTenantId).get("fs.defaultFS");
    }

    public static void setDefaultConf(Configuration conf) {
        conf.setBoolean(FS_HDFS_IMPL_DISABLE_CACHE, true);
        conf.setBoolean(IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED, true);
        conf.set(HadoopConfTool.FS_HDFS_IMPL, HadoopConfTool.DEFAULT_FS_HDFS_IMPL);
    }

    public static void setClusterService(ClusterService clusterService) {
        HadoopConf.clusterService = clusterService;
    }

    public static Configuration getDefaultHadoopConfiguration() {
        return defaultConfiguration;
    }

    public static YarnConfiguration getDefaultYarnConfiguration() {
        return defaultYarnConfiguration;
    }
}
