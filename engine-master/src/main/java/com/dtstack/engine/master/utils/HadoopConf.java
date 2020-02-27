package com.dtstack.engine.master.utils;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.hadoop.HadoopConfTool;
import com.dtstack.dtcenter.common.hadoop.YarnConfTool;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.google.common.base.Strings;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HadoopConf {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopConf.class);

    private final static String HADOOP_CONF_DIR = System.getenv("HADOOP_CONF_DIR");

    private static final List<String> HDFS_PRE = Arrays.asList("dfs", "fs", "hadoop");
    private static final List<String> YARN_PRE = Arrays.asList("yarn", "hadoop.security.authentication", "hadoop.security.authorization");

    private static volatile Configuration defaultConfiguration = null;

    private static volatile YarnConfiguration defaultYarnConfiguration = null;

    private static final Object initLock = new Object();

    private Configuration configuration;

    private YarnConfiguration yarnConfiguration;

    private static void initDefaultConfig() {

        if (defaultConfiguration == null) {
            synchronized (initLock) {
                if (defaultConfiguration != null) {
                    return;
                }

                try {
                    defaultConfiguration = new Configuration(false);
                    File dirFile = new File(ConfigConstant.LOCAL_HADOOP_CONF_DIR);
                    if (!dirFile.exists()) {
                        if (StringUtils.isNotBlank(HADOOP_CONF_DIR)) {
                            dirFile = new File(HADOOP_CONF_DIR);
                        } else {
                            LOG.error("-----------Directory 'user.dir/conf/hadoop' not exist and not set env for HADOOP_CONF_DIR!!!");
                        }
                    }

                    if (!dirFile.isDirectory()) {
                        LOG.error("HADOOP_CONF_DIR:{} is not dir.", dirFile.getAbsolutePath());
                    } else {
                        defaultConfiguration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
                        File[] xmlFileList = dirFile.listFiles(new FilenameFilter() {
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
                    }
                    defaultYarnConfiguration = new YarnConfiguration(defaultConfiguration);
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }
        }
    }

    public static List<File> getDefaultXML() {
        try {
            String dir = StringUtils.isNotBlank(HADOOP_CONF_DIR) ? HADOOP_CONF_DIR : ConfigConstant.LOCAL_HADOOP_CONF_DIR;
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                LOG.error("-----------not set env for HADOOP_CONF_DIR!!!");
            } else if (!dirFile.isDirectory()) {
                LOG.error("HADOOP_CONF_DIR:{} is not dir.", dir);
            } else {
                File[] xmlFileList = new File(dir).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.endsWith(".xml") || name.endsWith(".conf") || name.endsWith(".sh")) {
                            return true;
                        }
                        return false;
                    }
                });
                return Arrays.asList(xmlFileList);
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
        return null;
    }

    public HadoopConf() {

    }

    public void initHadoopConf(Map<String, Object> conf) {

        if (conf == null || conf.size() == 0) {
            //读取环境变量--走默认配置
            configuration = getDefaultConfiguration();
            return;
        }

        configuration = new Configuration();
        JSONObject hadoopConf = HadoopConf.getHadoopConf(conf);
        for (String key : hadoopConf.keySet()) {
            configuration.set(key, hadoopConf.getString(key));
        }
    }

    public static JSONObject getHadoopConf(Map<String, Object> conf) {

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



    private static boolean checkKeyUseful(String key, Object value, List<String> keySufList) {
        for (String suf : keySufList) {
            if (key.startsWith(suf) && value != null) {
                return true;
            }
        }
        return false;
    }

    public static void setKerberosConf(JSONObject hadoopConf, Map<String, Object> conf) {
        boolean authorization = MapUtils.getBoolean(conf, HadoopConfTool.IS_HADOOP_AUTHORIZATION, false);
        if (authorization) {
            hadoopConf.put(HadoopConfTool.IS_HADOOP_AUTHORIZATION, true);

            if (MapUtils.getString(conf, HadoopConfTool.HADOOP_AUTH_TYPE) != null) {
                hadoopConf.put(HadoopConfTool.HADOOP_AUTH_TYPE, MapUtils.getString(conf, HadoopConfTool.HADOOP_AUTH_TYPE));
            }

            if (MapUtils.getString(conf, HadoopConfTool.HIVE_BIND_HOST) != null) {
                hadoopConf.put(HadoopConfTool.HIVE_BIND_HOST, MapUtils.getString(conf, HadoopConfTool.HIVE_BIND_HOST));
            }

            if (MapUtils.getString(conf, HadoopConfTool.BEELINE_PRINCIPAL) != null) {
                hadoopConf.put(HadoopConfTool.BEELINE_PRINCIPAL, MapUtils.getString(conf, HadoopConfTool.BEELINE_PRINCIPAL));
            }

            if (MapUtils.getString(conf, HadoopConfTool.DFS_HTTP_POLICY) != null) {
                hadoopConf.put(HadoopConfTool.DFS_HTTP_POLICY, MapUtils.getString(conf, HadoopConfTool.DFS_HTTP_POLICY));
            }

            if (MapUtils.getString(conf, HadoopConfTool.DFS_DATA_TRANSFER_PROTECTION) != null) {
                hadoopConf.put(HadoopConfTool.DFS_DATA_TRANSFER_PROTECTION, MapUtils.getString(conf, HadoopConfTool.DFS_DATA_TRANSFER_PROTECTION));
            }

            if (MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_HOSTS) != null) {
                hadoopConf.put(HadoopConfTool.HADOOP_PROXYUSER_ADMIN_HOSTS, MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_HOSTS));
            }

            if (MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_GROUPS) != null) {
                hadoopConf.put(HadoopConfTool.HADOOP_PROXYUSER_ADMIN_GROUPS, MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_GROUPS));
            }

            for (Map.Entry<String, Object> keyVal : conf.entrySet()) {
                if (keyVal.getKey().endsWith(".principal") && keyVal.getKey().startsWith("dfs") && keyVal.getValue() != null) {
                    hadoopConf.put(keyVal.getKey(), keyVal.getValue().toString());
                } else if (keyVal.getKey().contains(".keytab") && keyVal.getKey().startsWith("dfs") && keyVal.getValue() != null) {
                    hadoopConf.put(keyVal.getKey(), keyVal.getValue().toString());
                }
            }
        }
    }

    public static void setKerberosConf(Configuration hadoopConf, Map<String, Object> conf) {
        boolean authorization = MapUtils.getBoolean(conf, HadoopConfTool.IS_HADOOP_AUTHORIZATION, false);
        if (authorization) {
            hadoopConf.set(HadoopConfTool.IS_HADOOP_AUTHORIZATION, "true");

            hadoopConf.set("dfs.data.transfer.protection", "integrity");

            if (MapUtils.getString(conf, HadoopConfTool.HADOOP_AUTH_TYPE) != null) {
                hadoopConf.set(HadoopConfTool.HADOOP_AUTH_TYPE, MapUtils.getString(conf, HadoopConfTool.HADOOP_AUTH_TYPE));
            }

            if (MapUtils.getString(conf, HadoopConfTool.HIVE_BIND_HOST) != null) {
                hadoopConf.set(HadoopConfTool.HIVE_BIND_HOST, MapUtils.getString(conf, HadoopConfTool.HIVE_BIND_HOST));
            }

            if (MapUtils.getString(conf, HadoopConfTool.BEELINE_PRINCIPAL) != null) {
                hadoopConf.set(HadoopConfTool.BEELINE_PRINCIPAL, MapUtils.getString(conf, HadoopConfTool.BEELINE_PRINCIPAL));
            }

            if (MapUtils.getString(conf, HadoopConfTool.DFS_HTTP_POLICY) != null) {
                hadoopConf.set(HadoopConfTool.DFS_HTTP_POLICY, MapUtils.getString(conf, HadoopConfTool.DFS_HTTP_POLICY));
            }

            if (MapUtils.getString(conf, HadoopConfTool.DFS_DATA_TRANSFER_PROTECTION) != null) {
                hadoopConf.set(HadoopConfTool.DFS_DATA_TRANSFER_PROTECTION, MapUtils.getString(conf, HadoopConfTool.DFS_DATA_TRANSFER_PROTECTION));
            }

            if (MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_HOSTS) != null) {
                hadoopConf.set(HadoopConfTool.HADOOP_PROXYUSER_ADMIN_HOSTS, MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_HOSTS));
            }

            if (MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_GROUPS) != null) {
                hadoopConf.set(HadoopConfTool.HADOOP_PROXYUSER_ADMIN_GROUPS, MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_GROUPS));
            }

            for (Map.Entry<String, Object> keyVal : conf.entrySet()) {
                if (keyVal.getKey().endsWith(".principal") && keyVal.getValue() != null) {
                    hadoopConf.set(keyVal.getKey(), keyVal.getValue().toString());
                } else if (keyVal.getKey().contains(".keytab") && keyVal.getValue() != null) {
                    hadoopConf.set(keyVal.getKey(), keyVal.getValue().toString());
                }
            }
        }
    }

    public static JSONObject getDefaultHadoopConf() {

        JSONObject hadoopConf = new JSONObject();

        try {
            Configuration conf = getDefaultConfiguration();
            String nameServices = HadoopConfTool.getDfsNameServices(conf);
            if (StringUtils.isNotBlank(nameServices)) {
                String haNameNodesKey = HadoopConfTool.getDfsHaNameNodesKey(conf);
                String haNameNodesVal = HadoopConfTool.getDfsHaNameNodes(conf, haNameNodesKey);
                String proxyProviderKey = HadoopConfTool.getClientFailoverProxyProviderKey(conf);
                String proxyProvider = HadoopConfTool.getClientFailoverProxyProviderVal(conf, proxyProviderKey);
                List<String> nnRpcAddressList = HadoopConfTool.getDfsNameNodeRpcAddressKeys(conf);

                hadoopConf.put(HadoopConfTool.DFS_NAME_SERVICES, nameServices);
                hadoopConf.put(haNameNodesKey, haNameNodesVal);
                //配置自动故障切换实现方式
                hadoopConf.put(proxyProviderKey, proxyProvider);
                nnRpcAddressList.forEach(key -> {
                    String val = HadoopConfTool.getDfsNameNodeRpcAddress(conf, key);
                    hadoopConf.put(key, val);
                });
            }
            String defaultFs = HadoopConfTool.getFSDefaults(conf);
            hadoopConf.put(HadoopConfTool.FS_DEFAULTFS, defaultFs);

            //非必须:针对hdfs的文件系统实现
            String fsHdfsImpl = HadoopConfTool.getFsHdfsImpl(conf);
            hadoopConf.put(HadoopConfTool.FS_HDFS_IMPL, fsHdfsImpl);
            //非必须:如果多个hadoopclient之间不互相影响需要取消cache
            String disableCache = HadoopConfTool.getFsHdfsImplDisableCache(conf);
            hadoopConf.put(HadoopConfTool.FS_HDFS_IMPL_DISABLE_CACHE, disableCache);

            Map<String, Object> allConf = new HashMap<>();
            for (Map.Entry<String, String> keyVal : conf) {
                allConf.put(keyVal.getKey(), keyVal.getValue());
            }

            setKerberosConf(hadoopConf, allConf);
        } catch (Exception e) {
            LOG.error("init default hdfs  config error", e);
        }
        return hadoopConf;
    }

    public void initYarnConf(Map<String, Object> conf) {
        if (conf == null || conf.size() == 0) {
            //读取环境变量--走默认配置
            yarnConfiguration = getDefaultYarnConfiguration();
            return;
        }

        yarnConfiguration = new YarnConfiguration();
        JSONObject yarnConf = HadoopConf.getYarnConf(conf);
        for (String key : yarnConf.keySet()) {
            yarnConfiguration.set(key, yarnConf.getString(key));
        }
    }

    public static JSONObject getYarnConf(Map<String, Object> conf) {

        JSONObject yarnConf = new JSONObject();

        for (Map.Entry<String, Object> keyVal : conf.entrySet()) {
            if (checkKeyUseful(keyVal.getKey(), keyVal.getValue(), YARN_PRE)) {
                yarnConf.put(keyVal.getKey(), keyVal.getValue().toString());
            }
        }
        return yarnConf;
    }

    public static JSONObject getDefaultYarnConf() {
        JSONObject yarnConf = new JSONObject();
        try {
            YarnConfiguration conf = getDefaultYarnConfiguration();
            String haRmIds = YarnConfTool.getYarnResourcemanagerHaRmIds(conf);
            List<String> addressKeys = YarnConfTool.getYarnResourceManagerAddressKeys(conf);
            List<String> webAppAddrKeys = YarnConfTool.getYarnResourceManagerWebAppAddressKeys(conf);
            String haEnabled = YarnConfTool.getYarnResourcemanagerHaEnabled(conf);

            if (StringUtils.isNotBlank(haRmIds)) {
                yarnConf.put(YarnConfTool.YARN_RESOURCEMANAGER_HA_RM_IDS, haRmIds);
            }

            addressKeys.forEach(key -> {
                String rmMgrAddr = YarnConfTool.getYarnResourceManagerAddressVal(conf, key);
                yarnConf.put(key, rmMgrAddr);
            });

            webAppAddrKeys.forEach(key -> {
                String rmMgrWebAppAddr = YarnConfTool.getYarnResourceManagerWebAppAddressVal(conf, key);
                yarnConf.put(key, rmMgrWebAppAddr);

            });

            if (!Strings.isNullOrEmpty(conf.get(YarnConfTool.YARN_NODEMANAGER_REMOTE_APP_LOG_DIR))) {
                yarnConf.put(YarnConfTool.YARN_NODEMANAGER_REMOTE_APP_LOG_DIR, conf.get(YarnConfTool.YARN_NODEMANAGER_REMOTE_APP_LOG_DIR));
            }

            yarnConf.put(YarnConfTool.YARN_RESOURCEMANAGER_HA_ENABLED, haEnabled);
        } catch (Exception e) {
            LOG.error("init default yarn  config error", e);
        }
        return yarnConf;
    }

    public static Configuration getDefaultConfiguration() {
        if (defaultConfiguration == null) {
            initDefaultConfig();
        }
        return defaultConfiguration;
    }

    public static YarnConfiguration getDefaultYarnConfiguration() {
        if (defaultYarnConfiguration == null) {
            initDefaultConfig();
        }
        return defaultYarnConfiguration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getDefaultFs() {
        return configuration.get("fs.defaultFS");
    }

    public YarnConfiguration getYarnConfiguration() {
        return yarnConfiguration;
    }
}
