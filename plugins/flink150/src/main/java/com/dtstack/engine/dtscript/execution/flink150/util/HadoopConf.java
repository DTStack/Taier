package com.dtstack.engine.dtscript.execution.flink150.util;


import com.dtstack.engine.common.util.HadoopConfTool;
import com.dtstack.engine.common.util.YarnConfTool;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.util.Strings;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sishu.yss
 *
 */
public class HadoopConf {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopConf.class);

    private final static String HADOOP_CONF = System.getProperty("user.dir")+"/conf/hadoop/";

    private final static String HADOOP_CONF_DIR = System.getenv("HADOOP_CONF_DIR");

    private static volatile Configuration defaultConfiguration = null;

    private static volatile YarnConfiguration defaultYarnConfiguration = null;

    private static final Object initLock = new Object();

    private Configuration configuration;

	private YarnConfiguration yarnConfiguration;

    private static void initDefaultConfig(){

        if(defaultConfiguration == null){
            synchronized (initLock){
                if(defaultConfiguration != null){
                    return;
                }

                try {
                    defaultConfiguration = new Configuration();
                    defaultYarnConfiguration = new YarnConfiguration();
                    String dir = StringUtils.isNotBlank(HADOOP_CONF_DIR) ? HADOOP_CONF_DIR : HADOOP_CONF;
                    File dirFile = new File(dir);
                    if(!dirFile.exists()){
                        LOG.error("-----------not set env for HADOOP_CONF_DIR!!!");
                    }else if(!dirFile.isDirectory()){
                        LOG.error("HADOOP_CONF_DIR:{} is not dir.", dir);
                    }else{
                        defaultConfiguration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
                        File[] xmlFileList = new File(dir).listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                if(name.endsWith(".xml")) {
                                    return true;
                                }
                                return false;
                            }
                        });

                        if(xmlFileList != null) {
                            for(File xmlFile : xmlFileList) {
                                defaultConfiguration.addResource(xmlFile.toURI().toURL());
                                defaultYarnConfiguration.addResource(xmlFile.toURI().toURL());
                            }
                        }
                    }
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
        }
    }

    public HadoopConf(){

    }

    public void initHadoopConf(Map<String, Object> conf){

        if(conf == null || conf.size() == 0){
            //读取环境变量--走默认配置
            configuration = getDefaultConfiguration();
            return;
        }

        configuration = new Configuration();
        String nameServices = HadoopConfTool.getDfsNameServices(conf);
        if (StringUtils.isNotBlank(nameServices)){
            String haNameNodesKey = HadoopConfTool.getDfsHaNameNodesKey(conf);
            String haNameNodesVal = HadoopConfTool.getDfsHaNameNodes(conf, haNameNodesKey);
            String proxyProviderKey = HadoopConfTool.getClientFailoverProxyProviderKey(conf);
            String proxyProvider = HadoopConfTool.getClientFailoverProxyProviderVal(conf, proxyProviderKey);
            List<String> nnRpcAddressList = HadoopConfTool.getDfsNameNodeRpcAddressKeys(conf);

            configuration.set(HadoopConfTool.DFS_NAME_SERVICES, nameServices);
            configuration.set(haNameNodesKey, haNameNodesVal);
            //配置自动故障切换实现方式
            configuration.set(proxyProviderKey, proxyProvider);
            nnRpcAddressList.forEach(key -> {
                String val = HadoopConfTool.getDfsNameNodeRpcAddress(conf, key);
                configuration.set(key, val);
            });
        }

        String defaultFs = HadoopConfTool.getFSDefaults(conf);
        configuration.set(HadoopConfTool.FS_DEFAULTFS, defaultFs);

        //非必须:针对hdfs的文件系统实现
        String fsHdfsImpl = HadoopConfTool.getFsHdfsImpl(conf);
        configuration.set(HadoopConfTool.FS_HDFS_IMPL, fsHdfsImpl);
        //非必须:如果多个hadoopclient之间不互相影响需要取消cache
        String disableCache = HadoopConfTool.getFsHdfsImplDisableCache(conf);
        configuration.set(HadoopConfTool.FS_HDFS_IMPL_DISABLE_CACHE, disableCache);

        if(MapUtils.getString(conf, HadoopConfTool.DFS_HTTP_POLICY) != null){
            configuration.set(HadoopConfTool.DFS_HTTP_POLICY, MapUtils.getString(conf, HadoopConfTool.DFS_HTTP_POLICY));
        }

        if(MapUtils.getString(conf, HadoopConfTool.DFS_DATA_TRANSFER_PROTECTION) != null){
            configuration.set(HadoopConfTool.DFS_DATA_TRANSFER_PROTECTION, MapUtils.getString(conf, HadoopConfTool.DFS_DATA_TRANSFER_PROTECTION));
        }

        if(MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_HOSTS) != null){
            configuration.set(HadoopConfTool.HADOOP_PROXYUSER_ADMIN_HOSTS, MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_HOSTS));
        }

        if(MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_GROUPS) != null){
            configuration.set(HadoopConfTool.HADOOP_PROXYUSER_ADMIN_GROUPS, MapUtils.getString(conf, HadoopConfTool.HADOOP_PROXYUSER_ADMIN_GROUPS));
        }

        Boolean isAuth = MapUtils.getBoolean(conf, HadoopConfTool.IS_HADOOP_AUTHORIZATION, false);
        configuration.set(HadoopConfTool.IS_HADOOP_AUTHORIZATION, isAuth.toString());

        if(Strings.isNotEmpty(HadoopConfTool.getAuthType(conf))){
            configuration.set(HadoopConfTool.HADOOP_AUTH_TYPE, HadoopConfTool.getAuthType(conf));
        }

        for (Map.Entry<String, Object> keyVal : conf.entrySet()) {
            if(keyVal.getKey().contains(".principal") && keyVal.getValue() != null){
                configuration.set(keyVal.getKey(), keyVal.getValue().toString());
            }
        }
    }

    public void initYarnConf(Map<String, Object> conf){

        if(conf == null || conf.size() == 0){
            //读取环境变量--走默认配置
            yarnConfiguration = getDefaultYarnConfiguration();
            return;
        }

        String haRmIds = YarnConfTool.getYarnResourcemanagerHaRmIds(conf);
        List<String> addressKeys = YarnConfTool.getYarnResourceManagerAddressKeys(conf);
        List<String> webAppAddrKeys = YarnConfTool.getYarnResourceManagerWebAppAddressKeys(conf);
        String haEnabled = YarnConfTool.getYarnResourcemanagerHaEnabled(conf);

        yarnConfiguration = new YarnConfiguration(configuration);
        if (StringUtils.isNotBlank(haRmIds)) {
            yarnConfiguration.set(YarnConfTool.YARN_RESOURCEMANAGER_HA_RM_IDS, haRmIds);
        }

        addressKeys.forEach(key -> {
            String rmMgrAddr = YarnConfTool.getYarnResourceManagerAddressVal(conf, key);
            yarnConfiguration.set(key, rmMgrAddr);
        });

        webAppAddrKeys.forEach(key -> {
            String rmMgrWebAppAddr = YarnConfTool.getYarnResourceManagerWebAppAddressVal(conf, key);
            yarnConfiguration.set(key, rmMgrWebAppAddr);

        });

        yarnConfiguration.set(YarnConfTool.YARN_RESOURCEMANAGER_HA_ENABLED, haEnabled);//必要
    }

    public static Configuration getDefaultConfiguration() {
        if(defaultConfiguration == null){
            initDefaultConfig();
        }
        return defaultConfiguration;
    }

    public static YarnConfiguration getDefaultYarnConfiguration() {
        if(defaultYarnConfiguration == null){
            initDefaultConfig();
        }
        return defaultYarnConfiguration;
    }

    public Configuration getConfiguration(){
        return configuration;
    }

    public String getDefaultFs(){
        return configuration.get("fs.defaultFS");
    }

    public YarnConfiguration getYarnConfiguration() {
        return yarnConfiguration;
    }

}
