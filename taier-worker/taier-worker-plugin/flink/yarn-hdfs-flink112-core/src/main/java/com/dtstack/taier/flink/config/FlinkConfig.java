
package com.dtstack.taier.flink.config;

import com.dtstack.taier.base.BaseConfig;
import com.dtstack.taier.flink.constant.ConfigConstant;
import com.google.common.base.Strings;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sishu.yss
 * parameter doc: https://dtstack.yuque.com/rd-center/sm6war/mqef0g
 * relevant class
 * {@link com.dtstack.taier.flink.client.AbstractClientManager}
 * {@link com.dtstack.taier.flink.config.PluginConfig}
 */
public class FlinkConfig extends BaseConfig {

    private static List<String> ENGINE_FLINK_CONFIGS= initEngineFlinkConfigFields();

    /** chunjun dist jar directory*/
    private String chunjunDistDir;

    /** remote chunjun dist jar directory*/
    private String remoteChunjunDistDir;

    /** flink lib jar directory*/
    private String flinkLibDir;

    /** remote flink lib jar directory*/
    private String remoteFlinkLibDir;

    private String typeName;

    private String flinkJobMgrUrl;

    private String clusterMode;

    private String cluster;

    private String queue;

    private Map<String, Object> hadoopConf;

    private Map<String, Object> yarnConf;

    private String elasticCapacity;

    private String yarnAccepterTaskNumber;

    private int asyncCheckYarnClientThreadNum = 3;

    private Map<String, String> kerberosConfig;

    private String flinkSessionName = "Flink_session";

    public static final String DEFAULT_QUEUE = "default";

    private int sessionRetryNum = 5;

    /** session start by engine*/
    private boolean sessionStartAuto = false;

    private boolean flinkHighAvailability = false;

    /** file load mode*/
    private String pluginLoadMode = "shipfile";


    private int checkSubmitJobGraphInterval = 60;

    private int monitorElectionWaitTime = 5 * 1000;

    private long submitTimeout = 5;

    private int zkConnectionTimeout = 5000;

    private int zkSessionTimeout = 5000;

    public int getZkConnectionTimeout() {
        return zkConnectionTimeout;
    }

    public void setZkConnectionTimeout(int zkConnectionTimeout) {
        this.zkConnectionTimeout = zkConnectionTimeout;
    }

    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public void setZkSessionTimeout(int zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    public int getAsyncCheckYarnClientThreadNum() {
        return asyncCheckYarnClientThreadNum;
    }

    public void setAsyncCheckYarnClientThreadNum(int asyncCheckYarnClientThreadNum) {
        this.asyncCheckYarnClientThreadNum = asyncCheckYarnClientThreadNum;
    }

    private String sessionCheckJarPath;

    public String getSessionCheckJarPath() {
        if(Strings.isNullOrEmpty(sessionCheckJarPath)){
            return ConfigConstant.DEFAULT_SESSION_CHECK_PATH;
        }
        return sessionCheckJarPath;
    }

    public void setSessionCheckJarPath(String sessionCheckJarPath) {
        this.sessionCheckJarPath = sessionCheckJarPath;
    }


    public int getSessionRetryNum() {
        return sessionRetryNum;
    }

    public void setSessionRetryNum(int sessionRetryNum) {
        this.sessionRetryNum = sessionRetryNum;
    }

    private boolean monitorAcceptedApp = false;

    public boolean getMonitorAcceptedApp() {
        return monitorAcceptedApp;
    }

    public void setMonitorAcceptedApp(boolean monitorAcceptedApp) {
        this.monitorAcceptedApp = monitorAcceptedApp;
    }

    public int getMonitorElectionWaitTime() {
        return monitorElectionWaitTime;
    }

    public void setMonitorElectionWaitTime(int monitorElectionWaitTime) {
        this.monitorElectionWaitTime = monitorElectionWaitTime;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getFlinkJobMgrUrl() {
        return flinkJobMgrUrl;
    }

    public void setFlinkJobMgrUrl(String flinkJobMgrUrl) {
        this.flinkJobMgrUrl = flinkJobMgrUrl;
    }

    public String getClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(String clusterMode) {
        this.clusterMode = clusterMode;
    }

    public Map<String, Object> getHadoopConf() {
        return hadoopConf;
    }

    public void setHadoopConf(Map<String, Object> hadoopConf) {
        this.hadoopConf = hadoopConf;
    }

    public Map<String, Object> getYarnConf() {
        return yarnConf;
    }

    public void setYarnConf(Map<String, Object> yarnConf) {
        this.yarnConf = yarnConf;
    }

    public String getFlinkLibDir() {
        return flinkLibDir;
    }

    public void setFlinkLibDir(String flinkLibDir) {
        this.flinkLibDir = flinkLibDir;
    }

    public void setElasticCapacity(String elasticCapacity) {
        this.elasticCapacity = elasticCapacity;
    }

    public void setYarnAccepterTaskNumber(String yarnAccepterTaskNumber) {
        this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
    }

    public Map<String, String> getKerberosConfig() {
        return kerberosConfig;
    }

    public void setKerberosConfig(Map<String, String> kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
    }

    public String getFlinkSessionName() {
        return flinkSessionName;
    }

    public void setFlinkSessionName(String flinkSessionName) {
        this.flinkSessionName = flinkSessionName;
    }

    public boolean getSessionStartAuto() {
        return sessionStartAuto;
    }

    public void setSessionStartAuto(boolean sessionStartAuto) {
        this.sessionStartAuto = sessionStartAuto;
    }

    public boolean getFlinkHighAvailability() {
        return flinkHighAvailability;
    }

    public void setFlinkHighAvailability(boolean flinkHighAvailability) {
        this.flinkHighAvailability = flinkHighAvailability;
    }

    public String getPluginLoadMode() {
        if (Strings.isNullOrEmpty(pluginLoadMode)) {
            return ConfigConstant.FLINK_PLUGIN_SHIPFILE_LOAD;
        }
        return pluginLoadMode;
    }

    public void setPluginLoadMode(String pluginLoadMode) {
        this.pluginLoadMode = pluginLoadMode;
    }


    public String getCluster() {
        return StringUtils.isBlank(cluster) ? DEFAULT_QUEUE : cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public boolean getElasticCapacity() {
        return StringUtils.isBlank(elasticCapacity) || Boolean.parseBoolean(elasticCapacity);
    }

    public int getYarnAccepterTaskNumber() {
        return StringUtils.isBlank(yarnAccepterTaskNumber) ? 1: NumberUtils.toInt(yarnAccepterTaskNumber,2);
    }

    public static List<String> getEngineFlinkConfigs() {
        return ENGINE_FLINK_CONFIGS;
    }

    public static void setEngineFlinkConfigs(List<String> engineFlinkConfigs) {
        ENGINE_FLINK_CONFIGS = engineFlinkConfigs;
    }

    public int getCheckSubmitJobGraphInterval() {
        return checkSubmitJobGraphInterval;
    }

    public void setCheckSubmitJobGraphInterval(int checkSubmitJobGraphInterval) {
        this.checkSubmitJobGraphInterval = checkSubmitJobGraphInterval;
    }

    private static List<String> initEngineFlinkConfigFields() {
        Class<BaseConfig> baseClazz = BaseConfig.class;
        Field[] baseConfigFields = baseClazz.getDeclaredFields();

        Class<FlinkConfig> FlinkConfigClazz = FlinkConfig.class;
        Field[] flinkConfigFields = FlinkConfigClazz.getDeclaredFields();

        Field[] fields = ArrayUtils.addAll(flinkConfigFields, baseConfigFields);
        List<String> engineFlinkConfigs = new ArrayList<>(fields.length);
        for (Field field : fields) {
            if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) != java.lang.reflect.Modifier.STATIC) {
                String name = field.getName();
                engineFlinkConfigs.add(name);
            }
        }
        return engineFlinkConfigs;
    }

    public long getSubmitTimeout() {
        return submitTimeout;
    }

    public void setSubmitTimeout(long submitTimeout) {
        this.submitTimeout = submitTimeout;
    }

    public String getRemoteFlinkLibDir() {
        return remoteFlinkLibDir;
    }

    public void setRemoteFlinkLibDir(String remoteFlinkLibDir) {
        this.remoteFlinkLibDir = remoteFlinkLibDir;
    }

    public String getChunjunDistDir() {
        return chunjunDistDir;
    }

    public void setChunjunDistDir(String chunjunDistDir) {
        this.chunjunDistDir = chunjunDistDir;
    }

    public String getRemoteChunjunDistDir() {
        return remoteChunjunDistDir;
    }

    public void setRemoteChunjunDistDir(String remoteChunjunDistDir) {
        this.remoteChunjunDistDir = remoteChunjunDistDir;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
